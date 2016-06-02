package gwt.interop.utils.server.valuetypes;

import gwt.interop.utils.shared.SharedDataTypesFactory;
import gwt.interop.utils.shared.valuetypes.NumberValue;

public class JavaNumberValue implements NumberValue {
    private double v;

    public JavaNumberValue(double v) {
        this.v = v;
    }

    public double asDouble() {
        return v;
    }

    public Double asDoubleObj() {
        return Double.valueOf(v);
    }

    public int asInt() {
        return (int)v;
    }

    public Integer asInteger() {
        return Integer.valueOf((int)v);
    }

    public Long asLong() {
        return (long)v;
    }

    public static NumberValue from(int i) {
        return SharedDataTypesFactory.createNumberValue(i);
    }

    public static NumberValue from(double d) {

        return SharedDataTypesFactory.createNumberValue(d);
    }

    public static NumberValue from(long l) {

        //Only support long values we can safely store as a double i.e. must be greater than -(2 power 52)
        if (l < -4503599627370496L)
            throw new IllegalArgumentException("Number too large to store as javascript Number");

        return SharedDataTypesFactory.createNumberValue(l);
    }
}
