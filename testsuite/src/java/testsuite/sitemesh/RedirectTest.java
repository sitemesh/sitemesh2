package testsuite.sitemesh;

import testsuite.tester.WebTest;
import com.meterware.httpunit.WebResponse;
import electric.xml.Document;

/**
 * Test no problems with redirections, forwards and includes.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public class RedirectTest extends WebTest {

  public void testRedirectedPage() throws Exception {
    checkPage( "/redirect/simple-redirect.jsp" );
  }

  public void testRequestDispatcherForwardedPage() throws Exception {
    checkPage( "/redirect/simple-rdforward.jsp" );
  }

  public void testRequestDispatcherIncludedPage() throws Exception {
    checkPage( "/redirect/simple-rdinclude.jsp" );
  }

  public void testPageContextForwardedPage() throws Exception {
    checkPage( "/redirect/simple-forward.jsp" );
  }

  public void testPageContextIncludedPage() throws Exception {
    checkPage( "/redirect/simple-include.jsp" );
  }

  private void checkPage(String path) throws Exception {
    WebResponse rs = wc.getResponse( baseUrl + path );
    Document doc = getDocument( rs );
    assertEquals( "[:: Another page ::]", rs.getTitle() );
    assertEquals( "A different page", doc.getElementWithId( "p1" ).getText().toString() );
    assertEquals( "footer", doc.getElementWithId( "footer" ).getText().toString() );
    assertEquals( "Another page", doc.getElementWithId( "header" ).getText().toString() );
  }

}
