package testsuite.sitemesh;

import testsuite.tester.WebTest;
import com.meterware.httpunit.WebResponse;
import electric.xml.Document;

/**
 * Test that the default welcome pages work ok (i.e. default.jsp).
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public class WelcomePageTest extends WebTest {

  public void testWelcomePage() throws Exception {
    checkPage( "/welcomepage/" );
	}

  public void testWelcomePageWithoutTrailingSlash() throws Exception {
    checkPage( "/welcomepage" );
	}

  private void checkPage( String path ) throws Exception {
    WebResponse rs = wc.getResponse( baseUrl + path );
		Document doc = getDocument( rs );
		assertEquals( "[:: Welcome Page ::]", rs.getTitle() );
		assertEquals( "Welcome to the page", doc.getElementWithId( "p1" ).getText().toString() );
		assertEquals( "footer", doc.getElementWithId( "footer" ).getText().toString() );
		assertEquals( "Welcome Page", doc.getElementWithId( "header" ).getText().toString() );
  }

}
