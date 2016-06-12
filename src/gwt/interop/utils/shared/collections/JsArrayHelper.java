package gwt.interop.utils.shared.collections;

import gwt.interop.utils.shared.collections.Array;

public class JsArrayHelper {

    public static native <T extends Object> Array<T> createArray() /*-{
        return [];
    }-*/;

    public static native <E> E[] castAsArray(Array src) /*-{
        return src;
    }-*/;

    /**
     * Set the Array value at the supplied index
     *
     * @param a
     * @param i
     * @param v
     * @param <T>
     */
    public static native <T> void setArrayValue(Array a, int i, T v) /*-{
        a[i] = v;
    }-*/;

    public static native <T> T getArrayValue(Array a, int i) /*-{
        return a[i];
    }-*/;
}
