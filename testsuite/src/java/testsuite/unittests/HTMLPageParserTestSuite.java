package testsuite.unittests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class HTMLPageParserTestSuite extends TestSuite {

    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();
        suite.addTest(HTMLPageParserTest.suite());
        suite.addTestSuite(PathMapperTest.class);
        suite.addTestSuite(ConfigLoaderTest.class);
        return suite;
    }
}