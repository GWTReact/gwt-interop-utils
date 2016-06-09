##Contents

1. Plain Javascript objects
2. Plain Javascript object support class
    * 2.1 Typeless objects
    * 2.2 Inline initialization
    * 2.3 Merging objects
    * 2.4 Removing properties from objects
3. Native Array support
    * 3.1 Standard Java Iterator and Stream Support
4. Simple Map Type
5. Value Types
6. Shared JSON compatible data structures
    * 6.1 JSON encoding/decoding
    * 6.2 Server Emulation

7. Common Functional Interfaces
8. Low level Javascript utilities

##1. Plain Javascript objects

Many javascript libraries make extensive use of plain Javascript objects to pass parameters and return results.
These are objects that constructed using <code>new Object()</code> or more commonly using literal initializers e.g.
<code>{a : 10, b : "Some value"}</code>.

In GWT you can create a plain Javascript object using JSNI as follows:

```java
    public native JavaScriptObject create(int a, String b) /*-{
      return { f1: a, f1: b };
    }-*/;
```

With the introduction of JsInterop, you can also create them by defining a Native JsType annotated class as follows:

```java
   @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
   static class TodoListState {
       String editingId;
       String newTodo;
   };

   TodoListState state = new TodoListState();
   state.editingId = 1;
   state.newTodo = "A new todo";
```

The important point to realize with these classes is that when you call <code>new</code> on them, the fields are NOT defined on
the object until you actually assign a value to them. So in the above example, after the call to <code>new</code>, state actually
points to a javascript object that looks like <code>{}</code>. After the two assignments, state will look like
<code>{editingId: 1, newTodo: "A new todo"}</code>.

Native JsType classes do have a few limitations. Firstly, you cannot define a constructor with any arguments.
To get around this, you can define a static factory method as follows:

```java
   @JsOverlay
   public static TodoListState create(int editingId, String newTodo) {
      TodoListState o = new TodoListState();
      o.editingId = editingId;
      o.newTodo = newTodo;
      return o;
   }
```
Secondly, if you subclass one of these types you need to make sure you include the
<code>@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")</code> annotation on
the subclass.

## 2. Plain Javascript object support class

gwt-interop-utils introduces the <code>JsPlainObj</code> class to make life easier to work with plain Javascript objects.
This class provides the following capabilities

####2.1 Typeless objects

You can define an arbritary object without defining an explicit class. This is useful for quickly converting JS code
before you introduce types e.g.

```java
   JsPlainObj state = new JsPlainObj();
   state.set("editingId", 1);
   state.set("newTodo", "A new todo")
```

####2.2 Inline initialization

Another capability that is very useful is inline initialization e.g.

```java
   import static gwt.interop.utils.client.plainobjects.PlainObj.$jsPlainObj;

   JsPlainObj state = $jsPlainObj("editingId", 1, "newTodo", "A new todo");
```


For initializing subclasses of JsPlainObj you can use the <code>$</code> method as follows:

```java
   import static gwt.interop.utils.client.plainobjects.PlainObj.$;

   SomeSubclassOfJsPlainObj mysubclass = $(new SomeSubclassOfJsPlainObj(), "editingId", 1, "newTodo", "A new todo");
```

Just a word of caution. Constructing plain Javascript objects in the above way isn't the most efficient approach.
In may cases you won't notice any problems. However, if using $ initializers is a problem then the better method
is to use use a typed class instead. If you want the fastest possible way then you will still need to use JSNI.


####2.3 Merging objects

One common pattern you will see in React code is to take a set of props and merge in additional props.
Typically for ES5 code they will use the <code>Object.assign</code> method and for ES2017 code they will use the object
spread operator <code>...</code> e.g.

```javascript
   //ES5
   var props = {a : 1, b : 1};
   var mergedProps = Object.assign({}, props , {b : 2, c : 3});

   // mergedProps will now be {a : 1, b : 2, c : 3}

   //ES2017 equivalent using the spread operator
   var mergedProps =  { ...props, b : 2, c : 3 };
```

Using JsPlainObj, you can achieve the same as follows:

```java
   JsPlainObj props = $jsPlainObj("a", 1, "b", 1);
   JsPlainObj mergedProps = props.merge($jsPlainObj("b", 2, "c", 3));
```

This will also work for JsType annotated classes that subclass <code>JsPlainObj</code>.

#### 2.4 Removing properties from objects

Another object operation you will see is where code consumes certain properties and then passes the
remaining properties onto another function. <code>JsPlainObj</code> provides the <code>except</code> method to support this:

```java
   JsPlainObj props = $jsPlainObj("a", 1, "b", 2, "c", 3, "d", 4);
   int a = props.getInt("a");
   int b = props.getInt("b");
   JsPlainObj remainingProps = props.except("a","b");

   // remainingProps will now be {c : 3, d : 4}
```

You can combine merge and except into a pipeline of operations by chaining them together:

```java
   JsPlainObj resulting = $jsPlainObj("a", 1, "b", 2, "c", 3, "d", 4)
                                .except("a","b")
                                .merge("f", 5, "g", 6);

   // resulting will now be {c : 3, d : 4, f : 5, g : 6}
```

## 3 Native Array support

gwt-interop-utils provides the <code>Array</code> Native JsType interface to allow code to work with the full
ES5 set of Array methods. ES5 introduced the the more functional style methods to arrays e.g. forEach, Map and Reduce.

```java
```

While GWT 2.8 will support the Java Stream API, the methods exposed by Array provide a more light weight and potentially faster
(for simple use cases) alternative. Being native to the browser, these methods don't require many thousands of lines
of emulation code. This can be important when trying to minimize code size on mobile devices.

#### 3.1 Standard Java Iterator and Stream Support

Using the <code>asIterable</code> method you can iterate over an instance of Array much like you would any other Java
collection e.g.

```java
        Array<String> a1 = Array.create();
        a1.push("value1");
        a2.push("value2")'
        StringBuilder test = new StringBuilder();

        for (String val : a1.asIterable()) {
            test.append(val);
        }
```

Once Streams support lands in GWT 2.8 a method to allow Array instances to optionally be used with streams will be provided.

## 4 Simple Map Type

The <code>StringMap</code> interface provides a simple string keyed map that is implemented as a Plain Javascript Object. This
has the advantage of being serializable to/from JSON in a natural form e.g.

```java
    StringMap<String> m1 = StringMap.create();
    m1.put("V1", "Value1");
    m1.put("V2", "Value2");
    
    Would serialize to the following JSON
    
    {
        "V1":"Value1", 
        "V2":"Value2"
    }
```

## 5 Value Types

The <code>NumberValue</code> JsNative interface allows code to operate with native Javascript Number objects. This
has a number of uses. Firstly, they can be used in generic methods/classes and still be passed to native Javascript
code which will see them as native numbers e.g.

```java
    Array<NumberValue> a1 = Array.create();
    a1.push(NumberValue.from(10));
    a1.push(NumberValue.from(20));

    This is equivalent to the Javascript array [10,20]
```

Secondly, they are useful for representing nullable numeric values that can be serialized to/from JSON.


## 6 Shared JSON compatible data structures

Given the Array, StringMap and NumberValue interfaces, you can create complex structures that can
freely be converted to/from JSON using the standard JSON parse/stringify methods. For example:

```java
import gwt.interop.utils.shared.collections.Array;
import gwt.interop.utils.shared.collections.StringMap;
import gwt.interop.utils.shared.valuetypes.NumberValue;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class CommonDataObject {
    public int intVal;
    public double doubleVal;
    public boolean booleanVal;
    public String stringVal;
    public NumberValue numberVal;
    public Array<String> anArray;
    public StringMap<String> aMap;
    public CommonDataObject2 embeddedObj;

    @JsOverlay
    public final String convolutedSharedMethod(String someArg) {
        StringBuilder o = new StringBuilder();

        anArray.forEachElem((e) -> {
            o.append(aMap.get(someArg));
            o.append(embeddedObj.field1);
            o.append(e);
        });

        return o.toString();
    }

    @JsOverlay
    public static CommonDataObject create() {
        CommonDataObject o = new CommonDataObject();
        o.intVal = 10;
        o.doubleVal = 20.20;
        o.booleanVal = true;
        o.stringVal = "A String Value";
        o.numberVal = NumberValue.from(10);
        o.anArray = Array.create();

        o.anArray.set(0, "ArrayValue1");
        o.anArray.set(2, "ArrayValue2");
        o.anArray.set(2, "ArrayValue3");

        o.aMap = StringMap.create();

        o.aMap.put("v1", "A Map Value 1");
        o.aMap.put("v2", "A Map Value 2");

        o.embeddedObj = new CommonDataObject2();
        o.embeddedObj.field1 = "An embbeded object field";
        return o;
    }
}
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class CommonDataObject2 {
    public String field1;
}
```

#### 6.1 JSON encoding/decoding

The <code>gwt.interop.utils.client.JSON.java</code> class provides an interface to the standard browser
JSON encoding/decoding functionality.

Given the <code>CommonDataObject</code> class, if you were to write the following:

```java
    CommonDataObject example = CommonDataObject.create();
    String jsonData = JSON.stringify(example);
```

jsonData would contain the following JSON:

```json
{
    "intVal":10,
    "doubleVal":20.2,
    "booleanVal":true,
    "stringVal":"A String Value",
    "numberVal":10,
    "anArray":[
        "ArrayValue1",
        "ArrayValue2",
        "ArrayValue3"
    ],
    "aMap":{
        "v1":"A Map Value 1", 
        "v2":"A Map Value 2"
    },
    "embeddedObj":{
        "field1":"An embbeded object field"
    }
}
```

The above JSON could equally be passed to <code>JSON.parse</code> and be assigned to
a <code>CommonDataObject</code> variable

```java
    CommonDataObject example = JSON.parse(jsonData);
```

#### 6.2 Server Emulation

An optional feature provided by gwt-interop-utils is the ability to use structures such as <code>CommonDataObject</code> both on
the client and server.

The exact same <code>CommonDataObject</code> class can be used on the server and all the methods would work as they do on
the client. On the server, the <code>Array.create()</code> method would instantiate the JavaArray
emulation class instead of a native javascript Array. Calling <code>StringMap.create()</code> instantiates
the JavaStringMap emulation class. <code>StringMap.create()</code>

For this mechanism to work, you need to specify different constructors when running on the client and server. For example, on the
client you would do the following:

```java
    StringMapFactory.setConstructor(JsStringMap::create);
    ArrayFactory.setConstructor(JsArray::create);
    NumericValueFactory.setConstructor(JsHelper::createNumber);
```

On the server you would instead specify the emulation classes

```java
        ArrayFactory.setConstructor(JavaArray::new);
        StringMapFactory.setConstructor(JavaStringMap::new);
        NumericValueFactory.setConstructor(JavaNumberValue::new);
```

## 7 Common Functional Interfaces

gwt-interop-utils provides a set of common functional interfaces annotated with @JsFunction, that can be used
by JsInterop libraries. 

| Interface | Description |
| :---      | :---  |
| JsProcedure | A callback that takes no arguments and returns nothing
| JsConsumer&lt;A> | A callback that accepts one argument of type A and has no return value
| JsBiConsumer&lt;A1, A2> |  A callback that accepts two arguments of type A1 and A2 and has no return value
| JsFunction&lt;R> | A function that has no arguments and returns R
| JsUnaryFunction&lt;R, A> | A function that has one argument A and returns R
| JsBiFunction&lt;R, A1, A2> | A function that has two arguments A1, A2 and returns R
| JsPredicate |  A function that tests some condition and returns true or false

## 8 Low level Javascript utilities

The <code>gwt.interop.utils.shared.JsHelper</code> class provides a set of low level functions for accessing/manipulating Javascript objects.