package gwt.interop.utils.shared.functional;

import jsinterop.annotations.JsFunction;

/**
 * A function that has three arguments A1, A2, A3 and returns R
 *
 * @param <R> The type of return
 * @param <A1> The type of Argument 1
 * @param <A2> The type of Argument 2
 * @param <A3> The type of Argument 3
 */
@JsFunction
public interface JsTriFunction<R, A1, A2, A3> {
    R call(A1 arg1, A2 arg2, A3 arg3);
}
