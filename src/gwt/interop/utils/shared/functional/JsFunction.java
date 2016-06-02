package gwt.interop.utils.shared.functional;

@jsinterop.annotations.JsFunction
public interface JsFunction<R, A> {
    R call(A arg);
}
