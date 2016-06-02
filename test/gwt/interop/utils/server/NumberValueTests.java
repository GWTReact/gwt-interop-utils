package gwt.interop.utils.server;

import gwt.interop.utils.server.valuetypes.JavaNumberValue;
import gwt.interop.utils.shared.SharedDataTypesFactory;
import gwt.interop.utils.shared.collections.ClientServerCommonNumberValueTests;

public class NumberValueTests {

    public static void main(String [] args) {
        SharedDataTypesFactory.setNumberValueConstructor(JavaNumberValue::new);

        ClientServerCommonNumberValueTests.run();
    }
}
