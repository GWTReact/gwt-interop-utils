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

import com.google.gwt.core.client.JavaScriptObject;
import gwt.interop.utils.shared.collections.Array;

/**
 * A set of low level Javascript functions useful for building JsInterop interfaces
 *
 * @author pstockley
 */
public class JsHelper {

    /**
     * Set the given int property on a javascript object
     *
     * @param o The object.
     * @param p The property to set
     * @param v The value
     * @param <O> The type of object (Must be either annotated with JsType or be based on
     *           JavaScriptObject
     */
    public static native <O> void setObjectProperty(O o, String p, int v) /*-{
        o[p] = v;
    }-*/;

    /**
     * Set the given double property on a javascript object
     *
     * @param o The object.
     * @param p The property to set
     * @param v The value
     * @param <O> The type of object (Must be either annotated with JsType or be based on
     *           JavaScriptObject
     */
    public static native <O> void setObjectProperty(O o, String p, double v)/*-{
        o[p] = v;
    }-*/;

    /**
     * Set the given boolean property on a javascript object
     *
     * @param o The object.
     * @param p The property to set
     * @param v The value
     * @param <O> The type of object (Must be either annotated with JsType or be based on
     *           JavaScriptObject
     */
    public static native <O> void setObjectProperty(O o, String p, boolean v)/*-{
        o[p] = v;
    }-*/;

    /**
     * Set the given property on a javascript object.
     *
     * @param o The object.
     * @param p The property to set
     * @param v The value
     * @param <O> The type of object (Must be either annotated with JsType or be based on
     *           JavaScriptObject
     */
    public static native <O> void setObjectProperty(O o, String p, Object v )/*-{
        o[p] = v;
    }-*/;


    public static native <O> int getObjectIntProperty(O o, String p) /*-{
        return o[p];
    }-*/;

    public static native <O, R> R getObjectProperty(O o, String p) /*-{
        return o[p];
    }-*/;

    public static native void removeProperty(Object o, String p) /*-{
        delete o[p];
    }-*/;

    public static native boolean hasProperty(Object o, String p) /*-{
        return o[p] !== undefined;
    }-*/;

    public static native boolean propertyIsFunction(Object o, String p) /*-{
        return o[p] !== undefined && typeof o[p] == 'function';
    }-*/;

    public static native Array<String> objectProperties(Object o) /*-{
        var p = [];

        for (var prop in o) {
            if (o.hasOwnProperty(prop)) p.push(prop);
        }

        return p;
    }-*/;

    public static native <T> Array<T> objectValues(Object o) /*-{
        var p = [];
        for (var prop in o) {
            if (o.hasOwnProperty(prop)) {
                p.push(o[prop]);
            }
        }
        return p;
    }-*/;

    public static native void applyPolyfills() /*-{
        if (typeof Object.assign == 'function') {
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
     }-*/;


    public static native <R> R merge(Object o1, Object o2) /*-{
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

    public static native <T> T unsafeCast(Object o) /*-{
        return o;
    }-*/;

    public static native int unsafeCastAsInt(Object o) /*-{
        return o;
    }-*/;
}