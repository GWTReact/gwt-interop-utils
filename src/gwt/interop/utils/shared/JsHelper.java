package gwt.interop.utils.shared;
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

import gwt.interop.utils.shared.collections.Array;

/**
 * A set of low level Javascript functions useful for building JsInterop interfaces
 *
 * @author pstockley
 */
public class JsHelper {

    /**
     * Returns is the specified property is defined as a function on a javascript object
     * @param o The object
     * @param p The property to test
     * @return true if the property exists and is a function
     */
    public static native boolean propertyIsFunction(Object o, String p) /*-{
        return o[p] !== undefined && typeof o[p] == 'function';
    }-*/;

    /**
     * Enumerate the property names of a javascript object
     *
     * @param o The object
     * @return An Array of property names
     */
    public static native Array<String> objectProperties(Object o) /*-{
        var p = [];

        for (var prop in o) {
            if (o.hasOwnProperty(prop)) {
                p.push(prop);
            }
        }

        return p;
    }-*/;

    /**
     * Enumerate the property values of a javascript object
     *
     * @param o The object
     * @param <T> The type of values
     * @return An Array of values
     */
    public static native <T> Array<T> objectValues(Object o) /*-{
        var p = [];
        for (var prop in o) {
            if (o.hasOwnProperty(prop)) {
                p.push(o[prop]);
            }
        }
        return p;
    }-*/;

    public static native <R> R merge(Object o1, Object o2) /*-{
        if (typeof Object.assign != 'function') {
            Object.assign = function (target) {
                'use strict';
                if (target == null) {
                    throw new TypeError('Cannot convert undefined or null to object');
                }

                target = Object(target);
                for (var index = 1; index < arguments.length; index++) {
                    var source = arguments[index];
                    if (source != null) {
                        for (var key in source) {
                            if (Object.prototype.hasOwnProperty.call(source, key)) {
                                target[key] = source[key];
                            }
                        }
                    }
                }
                return target;
            };
        }
        return Object.assign({}, o1, o2);
    }-*/;

    public static native <R> R except(Object o, String[] exclude) /*-{
        var out = {};
        var exludeMap = {};

        for(var i = 0; i < exclude.length; i++) {
            exludeMap[exclude[i]] = true;
        }

        for (var p in o) {
            if (!exludeMap[p] && o.hasOwnProperty(p)) {
                out[p] = o[p];
            }
        }

        return out;
    }-*/;
}