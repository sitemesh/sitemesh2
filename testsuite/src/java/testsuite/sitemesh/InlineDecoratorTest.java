package testsuite.sitemesh;

import com.meterware.httpunit.WebResponse;
import electric.xml.Document;
import testsuite.tester.WebTest;

/**
 *
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public class InlineDecoratorTest extends WebTest
{

    public void testPage1() throws Exception
    {
        WebResponse rs = wc.getResponse(server.getBaseURL() + "/inline/page1.jsp");
        Document doc = getDocument(rs);
        assertEquals("{inline1} content 1", rs.getTitle());
        assertEquals("page 1 content", doc.getElementWithId("bod").getText("p").toString());
        assertEquals("Inline internal content 1", doc.getElementWithId("inline1").getText().toString());
        assertEquals("Inline internal content 2", doc.getElementWithId("inline2").getText().toString());
        assertEquals("footer", doc.getElementWithId("footer").getText().toString());
    }

    public void testPage2() throws Exception
    {
        WebResponse rs = wc.getResponse(server.getBaseURL() + "/inline/page2.jsp");
        Document doc = getDocument(rs);
        assertEquals("{inline2} content 2", rs.getTitle());
        assertEquals("page 2 content", doc.getElementWithId("bod").getText("p").toString());
        assertEquals("Inline external content 1", doc.getElementWithId("inline1").getText().toString());
        assertEquals("panel 2", doc.getElementWithId("inline2").getText().toString());
        assertEquals("footer", doc.getElementWithId("footer").getText().toString());
    }

    public void testPage3() throws Exception
    {
        WebResponse rs = wc.getResponse(server.getBaseURL() + "/inline/page3.jsp");
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
    public void testPage5() throws Exception
    {
        WebResponse rs = wc.getResponse(server.getBaseURL() + "/inline/page5.jsp");
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
    public void testPage6() throws Exception
    {
        WebResponse rs = wc.getResponse(server.getBaseURL() + "/inline/page6.jsp");
        Document doc = getDocument(rs);
        assertEquals("{inline6} content 6", rs.getTitle());
        assertEquals("page 6 content", doc.getElementWithId("bod").getText("p").toString());
        assertEquals("Inline external content 6", doc.getElementWithId("inline1").getText().toString());
        assertEquals("\u0126\u0118\u0139\u0139\u0150", doc.getElementWithId("inline-content").getText().toString());
        assertEquals("footer", doc.getElementWithId("footer").getText().toString());
    }

}
