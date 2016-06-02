package gwt.interop.utils.shared;


import gwt.interop.utils.shared.collections.Array;
import gwt.interop.utils.shared.collections.StringMap;
import gwt.interop.utils.shared.valuetypes.NumberValue;
import jsinterop.annotations.JsType;

public class SharedDataTypesFactory {

    public interface ArrayConstructor {
        <T> Array<T> create();
    }

    public interface MapConstructor {
        <T> StringMap<T> create();
    }

    public interface NumberValueConstructor {
        NumberValue create(double v);
    }

    private static ArrayConstructor arrayConstructor;
    private static MapConstructor mapConstructor;
    private static NumberValueConstructor numberValueConstructor;


    public static void setArrayConstructor(ArrayConstructor arrayConstructor) {
        SharedDataTypesFactory.arrayConstructor = arrayConstructor;
    }

    public static void setMapConstructor(MapConstructor mapConstructor) {
        SharedDataTypesFactory.mapConstructor = mapConstructor;
    }

    public static void setNumberValueConstructor(NumberValueConstructor numberValueConstructor) {
        SharedDataTypesFactory.numberValueConstructor = numberValueConstructor;
    }


    public static <T> Array<T> createArray() {
        return arrayConstructor.create();
    }

    public static <T> StringMap<T> createMap() {
        return mapConstructor.create();
    }

    public static NumberValue createNumberValue(double d) {
        return numberValueConstructor.create(d);
    }
}
