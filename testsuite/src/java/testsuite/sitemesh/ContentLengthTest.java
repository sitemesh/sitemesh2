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

}
