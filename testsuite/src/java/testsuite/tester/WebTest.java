package testsuite.tester;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import org.xml.sax.SAXException;

import com.opensymphony.util.XMLUtils;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import junit.framework.TestCase;
import junit.framework.TestResult;
import testsuite.config.Server;
import testsuite.sitemesh.SiteMeshTestSuite;
import electric.xml.Document;
import electric.xml.ParseException;

/**
 * Extended JUnit TestCase. Has default constructor (for convenience), a setContext()
 * method called by the suite to set the name of the test, the WebConversation and the
 * Server.
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public class WebTest extends TestCase {

	/**
	 * The base URL of the server currently being tested against
	 */
	protected URL baseUrl;

	/**
	 * The WebConversation in use for the session.
	 */
	protected WebConversation wc;

    protected void setUp() throws Exception {
        super.setUp();
        baseUrl = SiteMeshTestSuite.currentBaseURL();
        wc = new WebConversation();
    }

	/**
	 * Convenience method - Use ElectricXML to access the HTML in the response for easy access.
	 */
	protected Document getDocument( WebResponse rs ) throws IOException, SAXException, ParseException {
		return new Document( new StringReader( XMLUtils.print( rs.getDOM() ) ) );
	}

    protected void assertStringContains(String expected, String actual) {
        assertTrue("Could not find <" + expected + "> in <" + actual + ">", actual.indexOf(expected) > -1);
    }
}
