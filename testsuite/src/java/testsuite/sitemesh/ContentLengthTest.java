package testsuite.sitemesh;

import com.meterware.httpunit.WebResponse;
import testsuite.tester.WebTest;

import java.io.IOException;

import org.xml.sax.SAXException;

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
        _testContentLength("/outputservlet?out=writer");
    }

    public void testContentMatchesContentLengthUsingStream() throws Exception {
        _testContentLength("/outputservlet?out=stream");
    }

     private void _testContentLength(String url) throws IOException, SAXException {
        WebResponse rs = wc.getResponse(server.getBaseURL() + url);
        final String contentLengthHeader = rs.getHeaderField("Content-length");
        if (contentLengthHeader != null) {
            final String contentLength = String.valueOf(rs.getText().getBytes().length);
            assertEquals("Content Length Header was '" + contentLengthHeader + "' but content length was '" + contentLength + "'",
                    contentLengthHeader, contentLength);
        }
    }

}
