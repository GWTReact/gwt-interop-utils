package gwt.interop.utils.shared.functional;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface JsBiFunction<R, A1, A2> {
    R call(A1 arg1, A2 arg2);
}
