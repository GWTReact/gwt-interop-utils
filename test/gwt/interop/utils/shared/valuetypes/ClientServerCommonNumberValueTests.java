package gwt.interop.utils.shared.valuetypes;

public class ClientServerCommonNumberValueTests {
    public static void run() {
        NumberValue v = NumberValue.from(100);

        assert(v.asInt() == 100);

        v = NumberValue.from(1000.1234455);
        assert(v.asDouble() == 1000.1234455);
    }
}
