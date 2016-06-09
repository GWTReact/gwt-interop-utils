package gwt.interop.utils.server;

import gwt.interop.utils.server.valuetypes.JavaNumberValue;
import gwt.interop.utils.shared.valuetypes.NumericValueFactory;
import gwt.interop.utils.shared.valuetypes.ClientServerCommonNumberValueTests;

public class NumberValueTests {

    public static void main(String [] args) {
        NumericValueFactory.setConstructor(JavaNumberValue::new);

        ClientServerCommonNumberValueTests.run();
    }
}
