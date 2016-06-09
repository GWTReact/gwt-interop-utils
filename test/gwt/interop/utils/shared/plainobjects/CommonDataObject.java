package gwt.interop.utils.shared.plainobjects;

import gwt.interop.utils.shared.collections.Array;
import gwt.interop.utils.shared.collections.StringMap;
import gwt.interop.utils.shared.valuetypes.NumberValue;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

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

        o.anArray.push("ArrayValue1");
        o.anArray.push("ArrayValue2");
        o.anArray.push("ArrayValue3");

        o.aMap = StringMap.create();

        o.aMap.put("v1", "A Map Value 1");
        o.aMap.put("v2", "A Map Value 2");

        o.embeddedObj = new CommonDataObject2();
        o.embeddedObj.field1 = "An embbeded object";
        return o;
    }
}
