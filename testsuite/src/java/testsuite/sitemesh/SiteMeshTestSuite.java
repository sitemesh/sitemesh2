package testsuite.sitemesh;

import com.meterware.httpunit.WebConversation;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.extensions.TestSetup;
import testsuite.config.ConfigException;
import testsuite.config.ConfigReader;
import testsuite.config.Server;
import testsuite.tester.WebTest;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Test suite for all web-app test cases.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public class SiteMeshTestSuite {

    private static Server currentServer;

    public static Test suite() throws Exception {
        TestSuite result = new TestSuite();
        File configFile = new File(System.getProperty("testsuite.config", "tests.xml"));
        ConfigReader config = new ConfigReader(configFile);
        for ( Iterator servers = config.getServers().iterator(); servers.hasNext(); ) {
            final Server server = (Server)servers.next();
            final TestSuite serverSuite = new TestSuite((server.getName() + " " + server.getVersion()).replaceAll("\\.", "_")) {
                public void run(TestResult result) {
                    currentServer = server;
                    super.run(result);
                }
            };
            buildSuite(serverSuite);
            result.addTest(serverSuite);
        }
        return result;
    }

    private static void buildSuite(TestSuite serverSuite) {
        serverSuite.addTestSuite(BasicPageTest.class);
        serverSuite.addTestSuite(WelcomePageTest.class);
        serverSuite.addTestSuite(SimpleDecoratorTest.class);
        serverSuite.addTestSuite(InlineDecoratorTest.class);
        serverSuite.addTestSuite(RedirectTest.class);
        serverSuite.addTestSuite(BinaryFileTest.class);
        serverSuite.addTestSuite(VelocityDecoratorTest.class);
        serverSuite.addTestSuite(FreemarkerDecoratorTest.class);
    }

    public static Server currentServer() {
        return currentServer;
    }

}
