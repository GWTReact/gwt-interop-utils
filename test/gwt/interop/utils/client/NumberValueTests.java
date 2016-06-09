package gwt.interop.utils.client;

import com.google.gwt.junit.client.GWTTestCase;
import gwt.interop.utils.shared.JsHelper;
import gwt.interop.utils.shared.valuetypes.NumericValueFactory;
import gwt.interop.utils.shared.valuetypes.ClientServerCommonNumberValueTests;

public class NumberValueTests extends GWTTestCase {

    public String getModuleName() {
        return "gwt.interop.utils.InteropUtils";
    }

    public void testNumbers() {
        NumericValueFactory.setConstructor(JsHelper::createNumber);

        ClientServerCommonNumberValueTests.run();
    }
}
