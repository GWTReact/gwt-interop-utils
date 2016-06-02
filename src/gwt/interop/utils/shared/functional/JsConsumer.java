package gwt.interop.utils.shared.functional;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface JsConsumer<A> {
    void accept(A arg);
}