package testsuite.sitemesh;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import testsuite.config.ConfigReader;
import testsuite.config.Server;
import testsuite.tester.Report;

import java.io.File;
import java.util.Iterator;

/**
 * Test suite for all web-app test cases.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public class SiteMeshTestSuite {

    private static Server currentServer;

    public static Test suite() throws Exception {
        final Report report = new Report(new File(System.getProperty("testsuite.results", "results.html")));
        TestSuite result = new TestSuite() {
            public void run(TestResult result) {
                result.addListener(report);
                report.startSuite();
                super.run(result);
                result.removeListener(report);
                report.endSuite();
            }
        };
        File configFile = new File(System.getProperty("testsuite.config", "tests.xml"));
        ConfigReader config = new ConfigReader(configFile);
        for ( Iterator servers = config.getServers().iterator(); servers.hasNext(); ) {
            final Server server = (Server)servers.next();
            final TestSuite serverSuite = new TestSuite((server.getName() + " " + server.getVersion()).replaceAll("\\.", "_")) {
                public void run(TestResult result) {
                    currentServer = server;
                    report.startServer(server);
                    super.run(result);
                    report.endServer();
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
