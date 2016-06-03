package gwt.interop.utils.shared.valuetypes;
/* The MIT License (MIT)

Copyright (c) 2016 GWT React

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE. */

import gwt.interop.utils.shared.JsHelper;
import gwt.interop.utils.shared.SharedDataTypesFactory;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * An interface to a Javascript Number object. The implementation may be different on the client
 * and server
 *
 * @author pstockley
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Number")
public interface NumberValue {
    @JsOverlay
    default double asDouble() { return JsHelper.castAsDouble(this); }

    @JsOverlay
    default int asInt() { return (int)JsHelper.castAsDouble(this); }

    @JsOverlay
    default long asLong() {
        return (long)JsHelper.castAsDouble(this);
    }

    @JsOverlay
    static NumberValue from(int i) {
        return SharedDataTypesFactory.createNumberValue(i);
    }

    @JsOverlay
    static NumberValue from(double d) {
        return SharedDataTypesFactory.createNumberValue(d);
    }

    @JsOverlay
    static NumberValue from(long l) {

        //Only support long values we can safely store as a double i.e. must be greater than -(2 power 52)
        if (l < -4503599627370496L)
            throw new IllegalArgumentException("Number too large to store as Javascript Number");

        return SharedDataTypesFactory.createNumberValue((double)l);
    }
}
