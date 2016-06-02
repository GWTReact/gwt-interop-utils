package gwt.interop.utils.client;

import com.google.gwt.junit.client.GWTTestCase;
import gwt.interop.utils.shared.JsHelper;
import gwt.interop.utils.shared.SharedDataTypesFactory;
import gwt.interop.utils.shared.collections.ClientServerCommonNumberValueTests;

public class NumberValueTests extends GWTTestCase {

    public String getModuleName() {
        return "gwt.interop.utils.InteropUtils";
    }

    public void testNumbers() {
        SharedDataTypesFactory.setNumberValueConstructor(JsHelper::createNumber);

        ClientServerCommonNumberValueTests.run();
    }
}
