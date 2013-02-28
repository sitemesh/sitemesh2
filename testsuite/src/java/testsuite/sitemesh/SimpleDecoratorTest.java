package testsuite.sitemesh;

import com.meterware.httpunit.WebResponse;
import electric.xml.Document;
import testsuite.tester.WebTest;

/**
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
 */
public class SimpleDecoratorTest extends WebTest {

	public void testCompleteHtmlPage() throws Exception {
		WebResponse rs = wc.getResponse( baseUrl + "/simple/page1.jsp" );
		Document doc = getDocument( rs );
		assertEquals( "[:: Simple page1 ::]", rs.getTitle() );
		assertEquals( "Hello world 1", doc.getElementWithId( "p1" ).getText().toString() );
		assertEquals( "footer", doc.getElementWithId( "footer" ).getText().toString() );
		assertEquals( "Simple page1", doc.getElementWithId( "header" ).getText().toString() );
	}

	public void testHtmlWithTitleAndBodyContentsOnly() throws Exception {
		WebResponse rs = wc.getResponse( baseUrl + "/simple/page2.jsp" );
		Document doc = getDocument( rs );
		assertEquals( "[:: Simple page2 ::]", rs.getTitle() );
		assertEquals( "Hello world 2", doc.getElementWithId( "p2" ).getText().toString() );
		assertEquals( "footer", doc.getElementWithId( "footer" ).getText().toString() );
		assertEquals( "Simple page2", doc.getElementWithId( "header" ).getText().toString() );
	}

	public void testHtmlWithBodyContentsOnly() throws Exception {
		WebResponse rs = wc.getResponse( baseUrl + "/simple/page3.jsp" );
		Document doc = getDocument( rs );
		assertEquals( "[:: MySite ::]", rs.getTitle() );
		assertEquals( "Hello world 3", doc.getElementWithId( "mainbody" ).getText().toString() );
		assertEquals( "footer", doc.getElementWithId( "footer" ).getText().toString() );
		assertEquals( "MySite", doc.getElementWithId( "header" ).getText().toString() );
	}

    /**
     * Internationalisation Test - using inline declaration of the page's encoding
     * @throws Exception
     */
    public void testDocumentWithInternationalizedCharactersUsingInlineEncodingDeclaration() throws Exception {
		WebResponse rs = wc.getResponse( baseUrl + "/simple/page4.jsp" );
		Document doc = getDocument( rs );
		assertEquals( "[:: MySite ::]", rs.getTitle() );
		assertEquals( "\u0126\u0118\u0139\u0139\u0150 world 4", doc.getElementWithId( "mainbody" ).getText().toString() );
		assertEquals( "footer", doc.getElementWithId( "footer" ).getText().toString() );
		assertEquals( "MySite", doc.getElementWithId( "header" ).getText().toString() );
	}

    /**
     * Internationalisation Test - using the encoding filter to specify the encoding.
     * <p>
     * If this test fails, but {@link #testDocumentWithInternationalizedCharactersUsingInlineEncodingDeclaration()} works
     * then you can still use your i18n'd application with sitemesh, but you need to specify the encoding in each page
     * @throws Exception
     */
    public void testDocumentWithInternationalizedCharactersUsingEncodingFilter() throws Exception {
		WebResponse rs = wc.getResponse( baseUrl + "/simple/page5.jsp" );
		Document doc = getDocument( rs );
		assertEquals( "[:: MySite ::]", rs.getTitle() );
		assertEquals( "\u0126\u0118\u0139\u0139\u0150 world 5", doc.getElementWithId( "mainbody" ).getText().toString() );
		assertEquals( "footer", doc.getElementWithId( "footer" ).getText().toString() );
		assertEquals( "MySite", doc.getElementWithId( "header" ).getText().toString() );
	}

    // Test to prove SIM-149 (ignore directories when using the FileDecoratorMapper)
    public void testFileDecoratorMapperExcludesDirectories() throws Exception {
        WebResponse rs = wc.getResponse( baseUrl + "/simple/page6.jsp" );
        Document doc = getDocument( rs );
        assertEquals( "[:: Simple page6 ::]", rs.getTitle() );
        assertEquals( "Hello world 6", doc.getElementWithId( "p1" ).getText().toString() );
        assertEquals( "footer", doc.getElementWithId( "footer" ).getText().toString() );
        assertEquals( "Simple page6", doc.getElementWithId( "header" ).getText().toString() );
    }

    /**
     * Tomcat 5 has problems serving static pages through sitemesh.  See SIM-74 and SIM-82.
     * This test case is to demonstrate this problem.
     * <p>
     * Note that this fails on Weblogic 7.0 SP4, as response.setContentType() is never called, even
     * though the content type is set by the server correctly to be text/html.  If you need this
     * functionality, you may be able to get around the problem by using a filter yourself to manually
     * set the content type for '*.html' files.
     */
    public void testStaticPage() throws Exception {
        WebResponse rs = wc.getResponse( baseUrl + "/simple/static.html" );
		Document doc = getDocument( rs );
		assertEquals( "[:: Simple page ::]", rs.getTitle() );
		assertEquals( "Hello world", doc.getElementWithId( "p" ).getText().toString() );
		assertEquals( "footer", doc.getElementWithId( "footer" ).getText().toString() );
		assertEquals( "Simple page", doc.getElementWithId( "header" ).getText().toString() );
    }

    public void testContentTypeSetCorrectly() throws Exception {
        WebResponse rs = wc.getResponse( baseUrl + "/simple/DifferentWaysOfSpecifyingContentType?approach=setContentType" );
		assertEquals( "[:: content-type ::]", rs.getTitle() );
    }

    public void testContentTypeSetUsingAddHeader() throws Exception {
        WebResponse rs = wc.getResponse( baseUrl + "/simple/DifferentWaysOfSpecifyingContentType?approach=addHeader" );
		assertEquals( "[:: content-type ::]", rs.getTitle() );
    }

    public void testContentTypeSetUsingSetHeader() throws Exception {
        WebResponse rs = wc.getResponse( baseUrl + "/simple/DifferentWaysOfSpecifyingContentType?approach=setHeader" );
		assertEquals( "[:: content-type ::]", rs.getTitle() );
    }

    public void testContentTypeSetMultipleTimes() throws Exception {
        WebResponse rs = wc.getResponse( baseUrl + "/simple/multiple-set-content-type.jsp" );
        assertStringContains("END", rs.getText());
        assertStringContains("MIDDLE", rs.getText());
        assertStringContains("START", rs.getText());
    }

    public void testDisablingDecorationWithRequestAttribute() throws Exception {
        WebResponse rs = wc.getResponse( baseUrl + "/simple/DifferentWaysOfSpecifyingContentType?approach=setContentType&kill=yes" );
        assertEquals("<html><head><title>content-type</title><body>body</body></html>", rs.getText().trim());
    }

    // Test that proves SIM-168 (error pages not decorated correctly)
    public void testErrorPageDecoratedCorrectly() throws Exception {
        WebResponse rs = wc.getResponse( baseUrl + "/basic/error-exception.jsp" );
        assertEquals( "[-- An error has occurred --]", rs.getTitle() );
    }


}
