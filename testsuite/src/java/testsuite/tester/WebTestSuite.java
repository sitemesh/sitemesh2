package testsuite.tester;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.lang.reflect.Method;

import com.meterware.httpunit.WebConversation;
import junit.framework.TestSuite;
import testsuite.config.ConfigException;
import testsuite.config.ConfigReader;
import testsuite.config.Server;

/**
 *
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public abstract class WebTestSuite extends TestSuite {

	private List testMethods = new ArrayList();

	protected abstract void setupTests();

	/**
	 * Build all tests from config file.
	 */
	protected TestSuite buildSuite() throws ConfigException, InstantiationException, IllegalAccessException {

		TestSuite suite = new TestSuite();

		// get config
		File configFile = new File( System.getProperty( "testsuite.config", "tests.xml" ) );
		ConfigReader config = new ConfigReader( configFile );

		// setup tests
		setupTests();

		// add each test for each server
		for ( Iterator i = testMethods.iterator(); i.hasNext(); ) {
			TestMethod testConfig = (TestMethod)i.next();
			for ( Iterator servers = config.getServers().iterator(); servers.hasNext(); ) {
				Server server = (Server)servers.next();
				WebConversation wc = new WebConversation();
				WebTest test = (WebTest)testConfig.cls.newInstance();
				test.setContext( testConfig.methodName, wc, server );
				suite.addTest( test );
			}
		}

		return suite;
	}

	/**
	 * Add a testXXX() methods of a WebTest class.
	 */
	protected final void addTest( Class cls, String methodName ) {
		TestMethod method = new TestMethod();
		method.cls = cls;
		method.methodName = methodName;
		testMethods.add( method );
	}

	/**
	 * Add all testXXX() methods of a WebTest class.
	 */
	protected final void addTests( Class cls ) {
		Method[] methods = cls.getMethods();
		for ( int i = 0; i < methods.length; i++ ) {
			Method method = methods[i];
			if ( method.getName().startsWith( "test" ) ) {
				addTest( cls, method.getName() );
			}
		}
	}

	private class TestMethod {
		Class cls;
		String methodName;
	}

}
