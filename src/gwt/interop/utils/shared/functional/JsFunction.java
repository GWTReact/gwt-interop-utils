package gwt.interop.utils.shared.functional;


/**
 * A function that has one argument T and returns R
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
@jsinterop.annotations.JsFunction
public interface JsFunction<T, R> {
	R apply(T t);
}
