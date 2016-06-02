package gwt.interop.utils.shared.functional;

import jsinterop.annotations.JsFunction;

/**
 * A callback that accepts three arguments of type A1, A2, A3 and has no return value
 *
 * @param <A1> The type of argument 1
 * @param <A2> The type of argument 2
 * @param <A3> The type of argument 3
 */
@JsFunction
public interface JsTriConsumer<A1, A2, A3> {
    void accept(A1 arg, A2 arg2, A3 arg3);
}
