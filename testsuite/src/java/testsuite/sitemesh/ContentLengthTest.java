package testsuite.sitemesh;

import com.meterware.httpunit.WebResponse;
import testsuite.tester.WebTest;

public class ContentLengthTest extends WebTest {

    public void testCompleteContentFromUndecoratedPageUsingWriter() throws Exception {
        WebResponse rs = wc.getResponse(server.getBaseURL() + "/outputservlet?out=writer");
        assertTrue("Document is not complete: " + rs.getText(), rs.getText().trim().endsWith("</html>"));
    }

    public void testCompleteContentFromUndecoratedPageUsingStream() throws Exception {
        WebResponse rs = wc.getResponse(server.getBaseURL() + "/outputservlet?out=stream");
        assertTrue("Document is not complete: " + rs.getText(), rs.getText().trim().endsWith("</html>"));
    }

    public void testContentMatchesContentLengthUsingWriter() throws Exception {
        WebResponse rs = wc.getResponse(server.getBaseURL() + "/outputservlet?out=writer");
        if (rs.getHeaderField("Content-length") != null) {
            assertEquals(rs.getHeaderField("Content-length"), String.valueOf(rs.getText().getBytes().length));
        }
    }

    public void testContentMatchesContentLengthUsingStream() throws Exception {
        WebResponse rs = wc.getResponse(server.getBaseURL() + "/outputservlet?out=stream");
        if (rs.getHeaderField("Content-length") != null) {
            assertEquals(rs.getHeaderField("Content-length"), String.valueOf(rs.getText().getBytes().length));
        }
    }

}
