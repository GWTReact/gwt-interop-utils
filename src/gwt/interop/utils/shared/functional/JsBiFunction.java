package gwt.interop.utils.shared.functional;

import jsinterop.annotations.JsFunction;

/**
 * A function that has two arguments A1, A2 and returns R
 *
 * @param <R> The type of return
 * @param <T> The type of Argument 1
 * @param <U> The type of Argument 2
 */
@JsFunction
public interface JsBiFunction<T, U, R> {
	R apply(T t, U u);
}
