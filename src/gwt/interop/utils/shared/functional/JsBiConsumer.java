package gwt.interop.utils.shared.functional;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface JsBiConsumer<A1, A2> {
    void accept(A1 arg, A2 arg2);
}