package gwt.interop.utils.server;

import gwt.interop.utils.server.collections.JavaArray;
import gwt.interop.utils.server.collections.JavaLinkedArray;
import gwt.interop.utils.server.collections.JavaStringMap;
import gwt.interop.utils.shared.SharedDataTypesFactory;
import gwt.interop.utils.shared.collections.*;

//TODO convert to Junit tests
public class CollectionsTests {

    public static void main(String [] args) {
        SharedDataTypesFactory.setArrayConstructor(JavaLinkedArray::new);
        SharedDataTypesFactory.setMapConstructor(JavaStringMap::new);

        //Run Client/Server Common tests for JavaLinkedArray
        ClientServerCommonArrayTests.run();

        SharedDataTypesFactory.setArrayConstructor(JavaArray::new);

        //Run Client/Server Common tests for JavaArray
        ClientServerCommonArrayTests.run();

        //Test JavaStringMap
        ClientServerCommonMapTests.run();
    }
}
