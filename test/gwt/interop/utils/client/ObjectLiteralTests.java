package gwt.interop.utils.client;

import com.google.gwt.junit.client.GWTTestCase;
import gwt.interop.utils.client.collections.JsArray;
import gwt.interop.utils.client.collections.JsStringMap;
import gwt.interop.utils.client.objectliterals.ObjLiteral;
import gwt.interop.utils.shared.SharedDataTypesFactory;
import gwt.interop.utils.shared.objectliterals.CommonDataObject;

import static gwt.interop.utils.client.objectliterals.ObjLiteral.$;
import static gwt.interop.utils.client.objectliterals.ObjLiteral.$literal;


public class ObjectLiteralTests extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "gwt.interop.utils.InteropUtils";
    }

    public void testInitLiteral() {
        ObjLiteral o1 = $literal("a", 1, "b", 10, "c", 20);
        assertEquals(o1.toJSONString(), "{\"a\":1,\"b\":10,\"c\":20}");
    }

    public void testInitLiteralWithSuppliedObject() {
        ObjLiteral o1 = $(new ObjLiteral(), "a", 2, "b", 20, "c", 30);

        assertEquals(o1.toJSONString(), "{\"a\":2,\"b\":20,\"c\":30}");
    }

    public void testConstructLiteral() {
        ObjLiteral o2 = new ObjLiteral();
        o2.set("b", 2);
        o2.set("d", 3);

        assertEquals(o2.toJSONString(), "{\"b\":2,\"d\":3}");
    }

    public void testMerge() {
        ObjLiteral o1 = $literal("a", 1, "b", 10, "c", 20);
        ObjLiteral o2 = $literal("b", 2, "d", 3);
        ObjLiteral o3 = o1.merge(o2);

        assertEquals(o3.toJSONString(), "{\"a\":1,\"b\":2,\"c\":20,\"d\":3}");
    }

    public void testExcept() {
        ObjLiteral o1 = $literal("a", 1, "b", 10, "c", 20);

        ObjLiteral o2 = o1.except("a","c");
        assertEquals(o2.toJSONString(), "{\"b\":10}");
    }

    public void testComplexLiteralToJson() {
        SharedDataTypesFactory.setMapConstructor(JsStringMap::create);
        SharedDataTypesFactory.setArrayConstructor(JsArray::create);

        CommonDataObject commonObj = CommonDataObject.create();
        String json = JSON.stringify(commonObj);

        assertEquals(json, "{\"intVal\":10,\"doubleVal\":20.2,\"booleanVal\":true,\"stringVal\":\"A String Value\",\"numberVal\":10,\"anArray\":[\"ArrayValue1\",\"ArrayValue2\",\"ArrayValue3\"],\"aMap\":{\"v1\":\"A Map Value 1\",\"v2\":\"A Map Value 2\"},\"embeddedObj\":{\"field1\":\"An embbeded object\"}}");
    }

    public void testComplexLiteralFromJson() {
        SharedDataTypesFactory.setMapConstructor(JsStringMap::create);
        SharedDataTypesFactory.setArrayConstructor(JsArray::create);

        CommonDataObject commonObj = JSON.parse("{\"intVal\":10,\"doubleVal\":20.2,\"booleanVal\":true,\"stringVal\":\"A String Value\",\"numberVal\":10,\"anArray\":[\"ArrayValue1\",\"ArrayValue2\",\"ArrayValue3\"],\"aMap\":{\"v1\":\"A Map Value 1\",\"v2\":\"A Map Value 2\"},\"embeddedObj\":{\"field1\":\"An embbeded object\"}}");
        assertEquals(commonObj.intVal, 10);
        assertEquals(commonObj.doubleVal,20.2);
        assertEquals(commonObj.booleanVal,true);
        assertEquals(commonObj.stringVal,"A String Value");
        assertEquals(commonObj.numberVal.asInt(),10);
        assertEquals(commonObj.aMap.get("v1"), "A Map Value 1");
        assertEquals(commonObj.anArray.get(1), "ArrayValue2");

        //Currently crashes, compiler issue (being fixed)
        //assertEquals(commonObj.convolutedSharedMethod("v2"), "ddd");
    }
}