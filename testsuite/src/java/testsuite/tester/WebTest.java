package testsuite.tester;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.SAXException;

import com.opensymphony.util.XMLUtils;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import junit.framework.TestCase;
import junit.framework.TestResult;
import testsuite.config.Server;
import electric.xml.Document;
import electric.xml.ParseException;

/**
 * Extended JUnit TestCase. Has default constructor (for convenience), a setContext()
 * method called by the suite to set the name of the test, the WebConversation and the
 * Server.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public class WebTest extends TestCase {

	/**
	 * The Server that is currently being tested.
	 */
	protected Server server;

	/**
	 * The WebConversation in use for the session.
	 */
	protected WebConversation wc;

	public WebTest() {
		super( null );
	}

	public void setContext( String testName, WebConversation wc, Server server ) {
		this.wc = wc;
		this.server = server;
		setName( testName );
	}

	/**
	 * Convenience method - Print something to stdout
	 */
	protected void print( Object o ) {
		System.out.println( o );
	}

	/**
	 * Convenience method - Use ElectricXML to access the HTML in the response for easy access.
	 */
	protected Document getDocument( WebResponse rs ) throws IOException, SAXException, ParseException {
		return new Document( new StringReader( XMLUtils.print( rs.getDOM() ) ) );
	}

	/**
	 * Override default run() method in TestCase and print extra information
	 * about current test and which server it's running on.
	 */
	public void run( TestResult result ) {
		String cls = getClass().getName().substring( getClass().getPackage().getName().length() + 1 );
		print( "\nRunning " + cls + "." + getName() + "() on " + server.getName() + " version " + server.getVersion() );
		super.run( result );
	}

}
