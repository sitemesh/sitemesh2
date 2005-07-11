package testsuite.sitemesh;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xml.sax.SAXException;

import com.meterware.httpunit.*;
import testsuite.tester.WebTest;

import javax.servlet.http.HttpServletResponse;

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

    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

    /**
     * Check that sitemesh does not break unmodified pages (SIM-186)
     */
    public void testNotModified() throws Exception {
        // get the page twice, and get it with an 'If-Modified-Since' header the second time
        WebResponse rs = wc.getResponse( baseUrl + "/basic/notmodified.html" );
        assertEquals("SiteMesh plain page", rs.getTitle());

        WebRequest wr = new GetMethodWebRequest(baseUrl + "/basic/notmodified.html");
        wr.setHeaderField("If-Modified-Since", new SimpleDateFormat(PATTERN_RFC1123).format(new Date()));
        rs = wc.getResponse(wr);

        if (200 == rs.getResponseCode()) {
            assertEquals("SiteMesh plain page", rs.getTitle());
        }
        else if (304 == rs.getResponseCode()) {
            // this is valid as well!
        }
        else {
            fail("Response code was " + rs.getResponseCode() + " was expecting 200 or 304");
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
        assertEquals("This is a plain page.", rs.getText().trim());
    }

}
