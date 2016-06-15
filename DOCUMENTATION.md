##Contents

1. Plain Javascript objects
2. Plain Javascript object support class
    * 2.1 Typeless objects
    * 2.2 Inline initialization
    * 2.3 Merging objects
    * 2.4 Removing properties from objects
3. Native Array support
    * 3.1 Java Iterator support
    * 3.2 Java Stream support
    * 3.2 Java List support
4. Simple Map Type
5. JSON compatible data structures
    * 5.1 JSON encoding/decoding
6. Server Collection Emulation
    * 6.1 Shared Methods
    * 6.2 Collection Factories 
7. Common Functional Interfaces
8. Low level Javascript utilities

##1. Plain Javascript objects

<p>Many javascript libraries make extensive use of plain Javascript objects to pass parameters and return results.
These are objects that constructed using <code>new Object()</code> or more commonly using literal initializers e.g.
<code>{a : 10, b : "Some value"}</code>.</p>

In GWT you can create a plain Javascript object using JSNI as follows:

```java
    public native JavaScriptObject create(int a, String b) /*-{
      return { f1: a, f1: b };
    }-*/;
```

<p>With the introduction of JsInterop, you can also create them by defining a Native JsType annotated class as follows:</p>

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

<p>The important point to realize with these classes is that when you call <code>new</code> on them, the fields are NOT defined on
the object until you actually assign a value to them. So in the above example, after the call to <code>new</code>, state actually
points to a javascript object that looks like <code>{}</code>. After the two assignments, state will look like
<code>{editingId: 1, newTodo: "A new todo"}</code>.</p>

<p>Native JsType classes do have a few limitations. Firstly, you cannot define a constructor with any arguments.
To get around this, you can define a static factory method as follows:</p>

```java
   @JsOverlay
   public static TodoListState create(int editingId, String newTodo) {
      TodoListState o = new TodoListState();
      o.editingId = editingId;
      o.newTodo = newTodo;
      return o;
   }
```
<p>Secondly, if you subclass one of these types you need to make sure you include the
<code>@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")</code> annotation on
the subclass.</p>

## 2. Plain Javascript object support class

<p>gwt-interop-utils introduces the <code>JsPlainObj</code> class to make life easier to work with plain Javascript objects.
This class provides the following capabilities:</p>

####2.1 Typeless objects

<p>You can define an arbritary object without defining an explicit class. This is useful for quickly converting JS code
before you introduce types e.g.</p>

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

<p>Just a word of caution. Constructing plain Javascript objects in the above way isn't the most efficient approach.
In may cases you won't notice any problems. However, if using $ initializers is a problem then the better method
is to use use a typed class instead. If you want the fastest possible way then you will still need to use JSNI.</p>


####2.3 Merging objects

<p>One common pattern you will see in React code is to take a set of props and merge in additional props.
Typically for ES5 code they will use the <code>Object.assign</code> method and for ES2017 code they will use the object
spread operator <code>...</code> e.g.</p>

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

<p>Another object operation you will see is where code consumes certain properties and then passes the
remaining properties onto another function. <code>JsPlainObj</code> provides the <code>except</code> method to support this:</p>

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

<p>gwt-interop-utils provides the <code>gwt.interop.utils.shared.collections.Array</code> Native JsType interface to allow code 
to work with the full ES5 set of Array methods. ES5 introduced the the more functional style methods to arrays e.g. 
forEach, filter, map and reduce.</p>

```java
    Array<String> a1 = JsArray.create();
    a1.push("value1");
    a1.push("value2");
    a1.push("Not a value");
    
    Array<String> a2 = a1.filter(e -> e.startsWith("value"))
            .map(e -> "(" + e + ")");
```

<p>While GWT 2.8 will support the Java Stream API, the methods exposed by <code>Array</code> provide a more light weight
and potentially faster (for simple use cases) alternative. Being native to the browser, these methods don't require 
many thousands of lines of emulation code. This can be important when trying to minimize code size on mobile devices.</p>

#### 3.1 Java Iterator Support

Using the <code>asIterable</code> method you can iterate over an instance of <code>Array</code> much like you would any 
other Java collection e.g.

```java
    Array<String> a1 = JsArray.create();
    a1.push("value1");
    a1.push("value2");
    StringBuilder test = new StringBuilder();

    for (String val : a1.asIterable()) {
        test.append(val);
    }
```

#### 3.2 Java Stream Support

Array instances can optionally be used with streams e.g.
```java
    Array<String> a1 = JsArray.create();
    a1.push("value1");
    a1.push("value2");
    test = new StringBuilder();

    a1.stream().forEach(test::append);
```

#### 3.3 Java List Support

<p>The <code>gwt.interop.utils.shared.collections.ArrayListAdapter</code> class allows you to treat an <code>Array</code>
as a Java <code>List</code>. Below shows examples of it's usage:</p>

```java
    Array<String> a1 = JsArray.create();
    a1.push("value1");
    
    //Using an ArrayListAdapter you can access/mutate an Array using the List interface methods
    List<String> a1List = new ArrayListAdapter(a1);
    
    a1List.add("value2");
    //value2 will both be reflected in a1List and a1
        
    //A shorthand way to create an adapter is to use the asList method of Array
    List<String> a1List2 = a1.asList();
```

<p>Because an <code>ArrayListAdapter</code> wraps an existing Array object, if you have two adapters for the same underlying
Array, you cannot do equality checks between the two adapters e.g.</p>

```java
    Array<String> a1 = JsArray.create();
    List<String> a1List1 = a1.asList();
    List<String> a1List2 = a1.asList();
    
    //Even though a1List and a2List both point to the same Array, the are not equal
    
    assert(a1List1 == a1List2); //Will fail
```



## 4 Simple Map Type

<p>The <code>gwt.interop.utils.shared.collections.StringMap</code> interface provides a simple string keyed map that is implemented 
as a Plain Javascript Object. This has the advantage of being serializable to/from JSON in a natural form e.g.</p>

```java
    StringMap<String> m1 = JsStringMap.create();
    m1.put("V1", "Value1");
    m1.put("V2", "Value2");
    
    Would serialize to the following JSON
    
    {
        "V1":"Value1", 
        "V2":"Value2"
    }
```


## 5 JSON compatible data structures

<p>Given the Array and StringMap interfaces, you can create complex structures that can
freely be converted to/from idiomatic JSON using the standard JSON parse/stringify methods. For example:</p>

```java
    import gwt.interop.utils.shared.collections.Array;
    import gwt.interop.utils.shared.collections.StringMap;
    
    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class CommonDataObject {
        public int intVal;
        public double doubleVal;
        //Double is synonymous with a Javascript Number object so can safely be convered to/from JSON
        public Double doubleObjVal;
        public boolean booleanVal;
        //Boolean is synonymous with a Javascript Boolean object so can safely be convered to/from JSON
        public Boolean booleanObjVal;
        public String stringVal;
        public Array<String> anArray;
        public StringMap<String> aMap;
        public CommonDataObject2 embeddedObj;
    }
    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class CommonDataObject2 {
        public String field1;
    }
```

<p>Note above the use of <code>Double</code> and <code>Boolean</code>. In GWT 2.8 these Java classes are eqivalent
to their Javascript counterparts so result in standard JSON. This does not however, work for other Java primitive wrappers 
such as <code>Integer</code>, <code>Long</code>, <code>Short</code> or <code>Byte</code></p>

#### 5.1 JSON encoding/decoding

<p>The <code>gwt.interop.utils.client.JSON.java</code> class provides an interface to the standard browser
JSON encoding/decoding functionality.</p>

Given the <code>CommonDataObject</code> class, if you were to write the following:

```java
    import gwt.interop.utils.client.collections.JsArray;
    import gwt.interop.utils.client.collections.JsStringMap;

    CommonDataObject o = new CommonDataObject();
    o.intVal = 10;
    o.doubleVal = 20.20;
    o.doubleObjVal = 20.20;
    o.booleanVal = true;
    o.booleanObjVal = true;
    o.stringVal = "A String Value";
    o.anArray = JsArray.create();

    o.anArray.push("ArrayValue1");
    o.anArray.push("ArrayValue2");
    o.anArray.push("ArrayValue3");

    o.aMap = JsStringMap.create();

    o.aMap.put("v1", "A Map Value 1");
    o.aMap.put("v2", "A Map Value 2");

    o.embeddedObj = new CommonDataObject2();
    o.embeddedObj.field1 = "An embbeded object";    
    
    String jsonData = JSON.stringify(o);
```

jsonData would contain the following JSON:

```json
{
    "intVal":10,
    "doubleVal":20.2,
    "doubleObjVal":20.2,
    "booleanVal":true,
    "booleanObjVal":true,
    "stringVal":"A String Value",
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

<p>The above JSON could equally be passed to <code>JSON.parse</code> and be assigned to
a <code>CommonDataObject</code> variable</p>

```java
    CommonDataObject example = JSON.parse(jsonData);
```

## 6 Server Collection Emulation

<p>An optional feature provided by gwt-interop-utils is the ability to use structures such as <code>CommonDataObject</code> both on
the client and server. The Following classes provide full emulation for the Js Native <code>Array</code> and 
<code>StringMap</code> interfaces</p>

| Class | Description |
| :---      | :---  |
| JavaArray | Implements the Array interface by wrapping an ArrayList object
| JavaLinkedArray | Implements the Array interface by wrapping a LinkedList object
| JavaStringMap | Implements the StringMap interface by wrapping a Map object

Using these classes you could construct a <code>CommonDataObject</code> on the server as follows:

```java
    import gwt.interop.utils.client.collections.JsArray;
    import gwt.interop.utils.client.collections.JsStringMap;
    import gwt.interop.utils.server.collections.JavaArray;
    import gwt.interop.utils.server.collections.JavaStringMap;
    
    CommonDataObject o = new CommonDataObject();
    o.intVal = 10;
    o.doubleVal = 20.20;
    o.doubleObjVal = 20.20;
    o.booleanVal = true;
    o.booleanObjVal = true;
    o.stringVal = "A String Value";
    o.anArray = new JavaArray();

    o.anArray.push("ArrayValue1");
    o.anArray.push("ArrayValue2");
    o.anArray.push("ArrayValue3");

    o.aMap = new JavaStringMap();

    o.aMap.put("v1", "A Map Value 1");
    o.aMap.put("v2", "A Map Value 2");

    o.embeddedObj = new CommonDataObject2();
    o.embeddedObj.field1 = "An embbeded object";    
    
    String jsonData = JSON.stringify(o);
```

Then using a library such as FasterXML/jackson, you could then serialize this to JSON and send it the client. 

If on the server side you want to manipulate the collections using the traditional Java API's you can use the following methods:

```java
    import gwt.interop.utils.client.collections.JsArray;
    import gwt.interop.utils.client.collections.JsStringMap;
    import gwt.interop.utils.server.collections.JavaArray;
    import gwt.interop.utils.server.collections.JavaStringMap;
    
    CommonDataObject o = new CommonDataObject();
    o.anArray = new JavaArray();

    //Access the Array using the Java List API (this is very efficient on the server)
    List<String> anArrayList = o.anArray.asList();
    anArrayList.add("ArrayValue1");
    anArrayList.add("ArrayValue2");
    anArrayList.add("ArrayValue3");

    //Wrap an existing ArrayList maintained on the server
    ArrayList<String> existingArrayList = new ArrayList<>();
    o.anArray = new JavaArray(existingArrayList);
    
    //Wrap an existing Map maintained on the server
    Map<String, String> existingMap = new HashMap<>();
    o.aMap = new JavaStringMap(existingMap);
```

####6.1 Shared Methods

<p>Using the server emulation classes you can now write methods on your Data Transfer Objects that can be called both on the
client and server</p>

```java
    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class CommonDataObject {
        public int intVal;
        public double doubleVal;
        public Double doubleObjVal; //This is synonymous with a javascript Number object
        public boolean booleanVal;
        public Boolean booleanObjVal; //This is synonymous with a javascript Boolean object
        public String stringVal;
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
    }
```

####6.2 Collection Factories

<p>If within your shared DTO methods you want to construct Array and StringMap instances, you can use
the factory methods provided by gwt-interop-utils e.g.</p>

```java
    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public class CommonDataObject {
        .
        .
    
        @JsOverlay
        public static CommonDataObject create() {
            CommonDataObject o = new CommonDataObject();
            o.intVal = 10;
            o.doubleVal = 20.20;
            o.doubleObjVal = 20.20;
            o.booleanVal = true;
            o.booleanObjVal = true;
            o.stringVal = "A String Value";
            o.anArray = ArrayFactory.create();
    
            o.anArray.push("ArrayValue1");
            o.anArray.push("ArrayValue2");
            o.anArray.push("ArrayValue3");
    
            o.aMap = StringMapFactory.create();
    
            o.aMap.put("v1", "A Map Value 1");
            o.aMap.put("v2", "A Map Value 2");
    
            o.embeddedObj = new CommonDataObject2();
            o.embeddedObj.field1 = "An embbeded object";
            return o;
        }
    }
```


<p>For the Factory mechanism to work, you need to specify different constructors when running on the client and server. For example, on the
client you would do the following:</p>

```java
    StringMapFactory.setConstructor(JsStringMap::create);
    ArrayFactory.setConstructor(JsArray::create);
```

On the server you would instead specify the emulation classes

```java
    ArrayFactory.setConstructor(JavaArray::new);
    StringMapFactory.setConstructor(JavaStringMap::new);
```

## 7 Common Functional Interfaces

<p>gwt-interop-utils provides a set of common functional interfaces annotated with @JsFunction, that can be used
by JsInterop libraries:</p>

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

<p>The <code>gwt.interop.utils.shared.JsHelper</code> class provides a set of low level functions for 
accessing/manipulating Javascript objects.</p>