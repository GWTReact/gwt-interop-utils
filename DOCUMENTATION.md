##Contents

1. Object Literals
2. Object literal support class
    * 2.1 Typeless literals
    * 2.2 Inline initialization
    * 2.3 Merging objects
    * 2.4 Removing properties from objects
3. Shared JSON compatible data structures
    * 3.1 JSON encoding/decoding
    3.2 Standard Java Collection Integration
    * 3.3 Defining client/server shared type factory methods
4. Common Functional Interfaces
5. JSON utilities 
6. Low level Javascript utilities

##1. Object literals

Many javascript libraries make extensive use of object literals (<code>{a : 10, b : "Some value"}</code>) 
to pass parameters and return results. They are also the foundation for the JSON prototcol in the browser.

In GWT, the closest we can get to defining literals is defining a JsType annotated class as follows

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

The important point to realize with these literal classes is that when you call new on them, the fields are NOT defined on
the object until you actually assign a value to them. So in the above example, after the call to new, state actually
points to a javascript object that looks like <code>{}</code>. After the two assignments, state will look like
<code>{editingId: 1, newTodo: "A new todo"}</code>.

Native JsType classes have a few limitations. Firstly, you cannot define a constructor with any arguments.
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

## 2. Object literal support class

gwt-interop-utils introduces the <code>ObjLiteral</code> class to make life easier to work with object literals. This provides
the following capabilities

####2.1 Typeless literals

You can define an arbritary literal without defining an explicit class. This is useful for quickly converting JS code
before you introduce types e.g.

```java
   ObjLiteral state = new ObjLiteral();
   state.set("editingId", 1);
   state.set("newTodo", "A new todo")
```

####2.2 Inline initialization

Another capability that is very useful is inline initialization e.g.

```java
   import static gwt.react.client.utils.ObjLiteral.$literal;

   ObjLiteral state = $literal("editingId", 1, newTodo", "A new todo");
```


For initializing subclasses of ObjLiteral you can use the <code>$</code> method as follows:

```java
   import static gwt.react.client.utils.ObjLiteral.$;

   SomeSubclassOfObjLiteral aLiteral = $(new SomeSubclassOfObjLiteral(), "editingId", 1, newTodo", "A new todo");
```

Just a word of caution. Constructing object literals in the above way isn't the most efficient approach. 
In may cases you won't notice any problems. However, if using $ initializers is a problem then the better method
is to use use a typed literal instead.

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

Using ObjLiteral, you can achieve the same as follows:

```java
   ObjLiteral props = $literal("a", 1, "b", 1);
   ObjLiteral mergedProps = props.merge($literal("b", 2, "c", 3));
```

This will also work for typed literals that subclass <code>ObjLiteral</code>.

#### 2.4 Removing properties from objects

Another object operation you will see is where code consumes certain properties and then passes the
remaining properties onto another function. <code>ObjLiteral</code> provides the <code>except</code> method to support this:

```java
   ObjLiteral props = $literal("a", 1, "b", 2, "c", 3, "d", 4);
   int a = props.getInt("a");
   int b = props.getInt("b");
   ObjLiteral remainingProps = props.except("a","b");

   // remainingProps will now be {c : 3, d : 4}
```

You can combine merge and except into a pipeline of operations by chaining them together:

```java
   ObjLiteral resulting = $literal("a", 1, "b", 2, "c", 3, "d", 4)
                                .except("a","b")
                                .merge("f", 5, "g", 6);

   // resulting will now be {c : 3, d : 4, f : 5, g : 6}
```

## 3 Shared JSON compatible data structures

gwt-interop-utils provides native JsType interfaces that allow you to build complex object literals that
can freely be converted to/from JSON using the standard JSON parse/stringify methods. In addition,
server emulation classes are provided so you can use the same interfaces both on the client and on the 
server. The interfaces provided are:
 
| Interface | Description |
| :---      | :---  |
| Array&lt;T&gt; | An ES5 compliant Array of T values. Includes foreach, map, reduce etc.
| StringMap&lt;T&gt; | A string keyed map of T values
| NumericValue | A Javascript Number implementation. Useful for representing nullable numeric values and for use in generic functions.

Given, these primitives you can define a class such as the following

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

#### 3.1 JSON encoding/decoding

Given the above class, if you were to write the following on the client :

```java
    CommonDataObject example = CommonDataObject.create();
    String jsonData = JSON.stringify(example);
```

jsonData would represent this object literal as the following json:

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
    
You could also do the opposite and feed the above json into JSON.parse and return
the exact same object literal.

```java
    CommonDataObject example = JSON.parse(jsonData);
```

The exact same <code>CommonDataObject</code> class can be used on the server and all the methods would work as they do on
the client. On the server, the <code>Array.create()</code> method would instantiate the JavaArray 
emulation class instead of a native javascript Array. Calling <code>StringMap.create()</code> instantiates
the JavaStringMap emulation class. <code>StringMap.create()</code>

#### 3.2 Standard Java Collection Integration

You can use an instance of Array much like you would any other Java collection, both on the client and server e.g.

```java
        Array<String> a1 = Array.create();
        a1.push("value1");
        a2.push("value2")'
        StringBuilder test = new StringBuilder();

        for (String val : a1.asIterable()) {
            test.append(val);
        }

        //Or with streams
        a1.stream().forEach(test::append);
```

PLEASE NOTE: streams will be supported once emulation lands in GWT 2.8

#### 3.3 Defining client/server factory methods

TODO

## 4 Common Functional Interfaces

gwt-interop-utils provides a set of functional interfaces annotated with @JsFunction, that can be used
by JsInterop libraries. 

| Interface | Description |
| :---      | :---  |
| JsProcedure | A callback that takes no arguments and returns nothing
| JsConsumer&lt;A> | A callback that accepts one argument of type A and has no return value
| JsBiConsumer&lt;A1, A2> |  A callback that accepts two arguments of type A1 and A2 and has no return value
| JsTriConsumer&lt;A1, A2, A3> |  A callback that accepts three arguments of type A1, A2, A3 and has no return value
| JsFunction&lt;R> | A function that has no arguments and returns R
| JsUnaryFunction&lt;R, A> | A function that has one argument A and returns R
| JsBiFunction&lt;R, A1, A2> | A function that has two arguments A1, A2 and returns R
| JsTriFunction&lt;R, A1, A2, A3> | A function that has three arguments A1, A2, A3 and returns R
| JsPredicate |  A function that tests some condition and returns true or false


## 5 JSON utilities

The <code>gwt.interop.utils.client.JSON.java</code> class provides an interface to the standard browser
JSON encoding/decoding functionality.

## 6 Low level Javascript utilities

The <code>gwt.interop.utils.shared.JsHelper</code> class provides a set of low level functions for accessing/manipulating Javascript objects.