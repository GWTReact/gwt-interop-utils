package gwt.interop.utils.shared.collections;
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
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * An interface for a simple javascript based map that maps a string key to an object
 * of type T. The actual implementation is different on the client and server
 *
 * @author pstockley
 *
 * @param <T> The type of map entry
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name="Object")
public interface StringMap<T> extends JsPropertyMap<T> {

    /**
     * Returns the keys defined in the map
     *
     * @return An Array of string keys
     */
    @JsOverlay default Array<String> keys() {
    	return JsHelper.objectProperties(this);
    }

    /**
     * Returns the values defined in the map
     *
     * @return An Array&lt;T&gt; of values
     */
    @JsOverlay default Array<T> values() {
        return JsHelper.objectValues(this);
    }


    /**
     * Removes all entries from this map
     */
    @JsOverlay default void clear() {
	    Js.asPropertyMap(this).forEach((key) -> delete(key));
    }

    /**
     * Returns the size of the map
     *
     * @return The size
     */
    @JsOverlay default int size() { return 1; }

    /**
     * Invokes the given callback for each key / value pair in the map
     *
     * @param forEachFn The callback to invoke
     */
    @JsOverlay default void forEach(ForEachFn<T> forEachFn) {
        Array<String> keys = keys();

        for(int i = 0; i < keys.getLength(); i++) {
            String key = keys.getAt(i);
            forEachFn.forEach(get(key), key, this);
        }
    }

    /**
     * Copies all entries from the provided ObservableMap into this map
     *
     * @param toMerge The StringMap to merge with this one
     */
    @JsOverlay default void merge(StringMap<T> toMerge) {
        toMerge.forEach((value, key, maps) -> set(key, value));
    }

    /**
     * A callback to invoke for each key / value pair in the map
     *
     * @param <T> The type of value
     */
    @JsFunction
    interface ForEachFn<T> {
        void forEach(T value, String key, StringMap<T> map);
    }
}
