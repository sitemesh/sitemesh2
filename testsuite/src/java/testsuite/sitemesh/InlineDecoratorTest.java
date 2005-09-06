package testsuite.sitemesh;

import com.meterware.httpunit.WebResponse;
import electric.xml.Document;
import electric.xml.Element;
import electric.xml.XPath;
import electric.xml.Elements;
import testsuite.tester.WebTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public class InlineDecoratorTest extends WebTest
{

    public void testInlineContentDecoratedInDecorator() throws Exception
    {
        WebResponse rs = wc.getResponse(baseUrl + "/inline/page1.jsp");
        Document doc = getDocument(rs);
        assertEquals("{inline1} content 1", rs.getTitle());
        assertEquals("page 1 content", doc.getElementWithId("bod").getText("p").toString());
        assertEquals("Inline internal content 1", doc.getElementWithId("inline1").getText().toString());
        assertEquals("Inline internal content 2", doc.getElementWithId("inline2").getText().toString());
        assertEquals("footer", doc.getElementWithId("footer").getText().toString());
    }

    public void testJspIncludedContentedDecoratedInDecorator() throws Exception
    {
        WebResponse rs = wc.getResponse(baseUrl + "/inline/page2.jsp");
        Document doc = getDocument(rs);
        assertEquals("{inline2} content 2", rs.getTitle());
        assertEquals("page 2 content", doc.getElementWithId("bod").getText("p").toString());
        assertEquals("Inline external content 1", doc.getElementWithId("inline1").getText().toString());
        assertEquals("panel 2", doc.getElementWithId("inline2").getText().toString());
        assertEquals("footer", doc.getElementWithId("footer").getText().toString());
    }

    public void testSitemeshIncludedContentDecoratedInDecorator() throws Exception
    {
        WebResponse rs = wc.getResponse(baseUrl + "/inline/page3.jsp");
        Document doc = getDocument(rs);
        assertEquals("{inline3} content 3", rs.getTitle());
        assertEquals("page 3 content", doc.getElementWithId("bod").getText("p").toString());
        assertEquals("Inline external content 1", doc.getElementWithId("inline1").getText().toString());
        assertEquals("panel 2", doc.getElementWithId("inline2").getText().toString());
        assertEquals("footer", doc.getElementWithId("footer").getText().toString());
    }

    /**
     * Test a page that uses an inline include, and is also decorated itself.
     * @throws Exception
     */
    public void testInlineContentedDecoratedInContent() throws Exception
    {
        WebResponse rs = wc.getResponse(baseUrl + "/inline/page5.jsp");
        Document doc = getDocument(rs);
        assertEquals("{inline5} content 5", rs.getTitle());
        assertEquals("page 5 content", doc.getElementWithId("bod").getText("p").toString());
        assertEquals("Inline external content 5", doc.getElementWithId("inline1").getText().toString());
        assertEquals("Some more inline stuff.", doc.getElementWithId("inline-content").getText().toString());
        assertEquals("footer", doc.getElementWithId("footer").getText().toString());
    }

    /**
     * Internationalisation test
     * @throws Exception
     */
    public void testInlineDecoratorWithInternationalizedCharacters() throws Exception
    {
        WebResponse rs = wc.getResponse(baseUrl + "/inline/page6.jsp");
        Document doc = getDocument(rs);
        assertEquals("{inline6} content 6", rs.getTitle());
        assertEquals("page 6 content", doc.getElementWithId("bod").getText("p").toString());
        assertEquals("Inline external content 6", doc.getElementWithId("inline1").getText().toString());
        assertEquals("\u0126\u0118\u0139\u0139\u0150", doc.getElementWithId("inline-content").getText().toString());
        assertEquals("footer", doc.getElementWithId("footer").getText().toString());
    }

    public void testIncludedContentFromOutputStreamAndWriterOnJspPage() throws Exception
    {
        WebResponse rs = wc.getResponse(baseUrl + "/inline/page7.jsp");
        Document doc = getDocument(rs);
        assertEquals("{inline7} content 7 jsp", rs.getTitle());
        assertEquals("Page 7 jsp content", doc.getElementWithId("bod").getText("p").toString());
        Elements inlineContents = doc.getElements(new XPath("//[@id='inline-content']"));
        assertEquals("This is a servlet using writer to output", inlineContents.next().getText().toString());
        assertEquals("This is a servlet using stream to output", inlineContents.next().getText().toString());
    }

    /**
     * Note that this fails on Weblogic 7.0 SP4 for the same reason as {@link SimpleDecoratorTest#testStaticPage()}.
     * @see SimpleDecoratorTest#testStaticPage() for more information
     */
    public void testIncludedContentFromOutputStreamAndWriterOnStaticPage() throws Exception
    {
        WebResponse rs = wc.getResponse(baseUrl + "/inline/page7.html");
        Document doc = getDocument(rs);
        assertEquals("{inline7} content 7 static", rs.getTitle());
        assertEquals("Page 7 static content", doc.getElementWithId("bod").getText("p").toString());
        Elements inlineContents = doc.getElements(new XPath("//[@id='inline-content']"));
        assertEquals("This is a servlet using writer to output", inlineContents.next().getText().toString());
        assertEquals("This is a servlet using stream to output", inlineContents.next().getText().toString());
    }

    public void testPageParametersSearchAncestryInsteadOfParent() throws Exception // SIM-119
    {
        WebResponse rs = wc.getResponse(baseUrl + "/inline/page8.jsp");
        assertTrue(rs.getText().indexOf("Inline Title") > 0);
    }

}
