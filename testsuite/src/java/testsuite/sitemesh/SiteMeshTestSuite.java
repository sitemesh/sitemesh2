package testsuite.sitemesh;

import junit.framework.Test;
import testsuite.tester.WebTestSuite;

/**
 * Test suite for all web-app test cases.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public class SiteMeshTestSuite extends WebTestSuite {

    public static Test suite() throws Exception {
        return new SiteMeshTestSuite().buildSuite();
    }

    protected void setupTests() {
        addTests( BasicPageTest.class );
        addTests( WelcomePageTest.class );
        addTests( SimpleDecoratorTest.class );
        addTests( InlineDecoratorTest.class );
        addTests( RedirectTest.class );
        addTests( BinaryFileTest.class );
        addTests( VelocityDecoratorTest.class );
    }

}
