package testsuite.sitemesh;

import com.meterware.httpunit.WebResponse;
import org.xml.sax.SAXException;
import testsuite.tester.WebTest;

/**
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public class ExcludesPatternTest extends WebTest {

    public void testExcludePattern() throws Exception {
        WebResponse rs = wc.getResponse(baseUrl + "/simple/exclude.jsp");
        assertNotDecorated(rs);

        rs = wc.getResponse(baseUrl + "/simple/exclude/page1.jsp");
        assertNotDecorated(rs);
    }

    public void testExcludeIsBasedOnServletPath() throws Exception {
        WebResponse rs = wc.getResponse(baseUrl + "/simple/exclude-partial/excluded");
        assertNotDecorated(rs);

        rs = wc.getResponse(baseUrl + "/simple/exclude-partial/notexcluded");
        assertIsDecorated(rs);
    }

    public void testExcludeTakesIntoAccountQueryString() throws Exception {
        WebResponse rs = wc.getResponse(baseUrl + "/simple/exclude-partial/?EXCLUDED");
        assertNotDecorated(rs);

        rs = wc.getResponse(baseUrl + "/simple/exclude-partial/?NOTEXCLUDED");
        assertIsDecorated(rs);
    }

    private void assertIsDecorated(WebResponse rs) throws SAXException {
        assertTrue("pattern should have been matched by <exclude> tag", rs.getTitle().startsWith("[::"));
    }

    private void assertNotDecorated(WebResponse rs) throws SAXException {
        assertFalse("pattern should NOT have been matched by <exclude> tag", rs.getTitle().startsWith("[::"));
    }
}
