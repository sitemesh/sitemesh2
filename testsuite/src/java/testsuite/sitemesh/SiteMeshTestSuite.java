package testsuite.sitemesh;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import testsuite.config.ConfigReader;
import testsuite.config.Server;
import testsuite.tester.Report;

import java.io.File;
import java.util.Iterator;
import java.net.URL;

/**
 * Test suite for all web-app test cases.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public class SiteMeshTestSuite {

    private static URL currentBaseUrl;

    public static class OnAllServers {
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
                        currentBaseUrl = server.getBaseURL();
                        report.startServer(server);
                        super.run(result);
                        report.endServer();
                    }
                };
                addTests(serverSuite);
                result.addTest(serverSuite);
            }
            return result;
        }
    }

    public static class OnEmbeddedServer {
        public static Test suite() throws Exception {
            final int port = Integer.parseInt(System.getProperty("testsuite.port", "9102"));
            final JettyWebServer server = new JettyWebServer(port, "dist/webapp");
            final URL baseUrl = new URL("http", "localhost", port, "");
            final TestSuite result = new TestSuite() {
                public void run(TestResult result) {
                    currentBaseUrl = baseUrl;
                    server.start();
                    super.run(result);
                    server.stop();
                }
            };
            addTests(result);
            return result;
        }
    }

    public static class OnEmbeddedTomcatServer {
        public static Test suite() throws Exception {
            final int port = Integer.parseInt(System.getProperty("testsuite.port", "9102"));
            String currentDirectory = new File("").getAbsolutePath();
            final TomcatWebServer server = new TomcatWebServer(port, currentDirectory + "/dist/webapp");
            final URL baseUrl = new URL("http", "localhost", port, "");
            final TestSuite result = new TestSuite() {
                public void run(TestResult result) {
                    currentBaseUrl = baseUrl;
                    server.start();
                    super.run(result);
                    server.stop();
                }
            };
            addTests(result);
            return result;
        }
    }

    private static void addTests(TestSuite serverSuite) {
        serverSuite.addTestSuite(SimpleDecoratorTest.class);
        serverSuite.addTestSuite(BasicPageTest.class);
        serverSuite.addTestSuite(ExcludesPatternTest.class);
        serverSuite.addTestSuite(WelcomePageTest.class);
        serverSuite.addTestSuite(ContentLengthTest.class);
        serverSuite.addTestSuite(InlineDecoratorTest.class);
        serverSuite.addTestSuite(RedirectTest.class);
        serverSuite.addTestSuite(BinaryFileTest.class);
        serverSuite.addTestSuite(VelocityDecoratorTest.class);
        serverSuite.addTestSuite(FreemarkerDecoratorTest.class);
        // serverSuite.addTestSuite(MultipassTest.class); Disabled until it can be wired into SM3. Was unreleased experimental stuff anyway. -joe
    }

    public static URL currentBaseURL() {
        return currentBaseUrl;
    }
}
