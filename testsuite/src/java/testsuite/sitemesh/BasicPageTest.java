package testsuite.sitemesh;

import java.io.IOException;
import java.net.MalformedURLException;

import org.xml.sax.SAXException;

import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.HttpInternalErrorException;
import com.meterware.httpunit.HttpNotFoundException;
import testsuite.tester.WebTest;

/**
 * Test basic capabilities of web-app - no sitemesh specific stuff.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public class BasicPageTest extends WebTest {

	/**
	 * Check plain page is working and contains correct title.
	 */
	public void testStandardJsp() throws Exception {
		WebResponse rs = wc.getResponse( baseUrl + "/basic/page.jsp" );
		assertEquals( 200, rs.getResponseCode() );
		assertEquals( "SiteMesh plain page", rs.getTitle() );
	}

	/**
	 * Check error 500 is properly sent.
	 */
	public void testErrorMessage() throws Exception {
		try {
			wc.getResponse( baseUrl + "/basic/error-500.jsp" );
			fail( "Expected 500" );
		}
		catch ( HttpInternalErrorException e ) {
		}
	}

	/**
	 * Check error 404 is properly sent.
	 */
	public void testNotFound() throws Exception {
		try {
			wc.getResponse( baseUrl + "/basic/dfddgdfvdf.jsp" );
			fail( "Expected 404" );
		}
		catch ( HttpNotFoundException e ) {
		}
	}

    public void testPlainTextPage() throws Exception {
        WebResponse rs = wc.getResponse( baseUrl + "/basic/text.jsp" );
        assertEquals("This is a plain page.", rs.getText().trim());
        assertEquals("text/plain", rs.getContentType());
    }

    public void testStaticTxtPage() throws Exception {
        WebResponse rs = wc.getResponse( baseUrl + "/basic/text.txt" );
        assertEquals("text/plain", rs.getContentType());
        assertEquals("This is a plain page.", rs.getText().trim());
    }

    public void testPageWithNoContentType() throws Exception {
        WebResponse rs = wc.getResponse( baseUrl + "/basic/text.unknown" );
        assertEquals("text/plain", rs.getContentType());
        assertEquals("This is a plain page.", rs.getText().trim());
    }

}
