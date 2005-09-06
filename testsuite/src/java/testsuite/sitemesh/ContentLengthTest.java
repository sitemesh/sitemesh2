package testsuite.sitemesh;

import com.meterware.httpunit.WebResponse;
import testsuite.tester.WebTest;

import java.io.IOException;

import org.xml.sax.SAXException;

public class ContentLengthTest extends WebTest {

    public void testCompleteContentFromUndecoratedPageUsingWriter() throws Exception {
        WebResponse rs = wc.getResponse(baseUrl + "/outputservlet?out=writer");
        assertTrue("Document is not complete: " + rs.getText(), rs.getText().trim().endsWith("</html>"));
    }

    public void testCompleteContentFromUndecoratedPageUsingStream() throws Exception {
        WebResponse rs = wc.getResponse(baseUrl + "/outputservlet?out=stream");
        assertTrue("Document is not complete: " + rs.getText(), rs.getText().trim().endsWith("</html>"));
    }

    public void testContentMatchesContentLengthUsingWriter() throws Exception {
        _testContentLength("/outputservlet?out=writer");
    }

    public void testContentMatchesContentLengthUsingStream() throws Exception {
        _testContentLength("/outputservlet?out=stream");
    }

     private void _testContentLength(String url) throws IOException, SAXException {
        WebResponse rs = wc.getResponse(baseUrl + url);
        final String contentLengthHeader = rs.getHeaderField("Content-length");
        if (contentLengthHeader != null) {
            final String contentLength = String.valueOf(rs.getText().getBytes().length);
            assertEquals("Content Length Header was '" + contentLengthHeader + "' but content length was '" + contentLength + "'",
                    contentLengthHeader, contentLength);
        }
    }

    public void testInternationalizationDoesNotTrimCharacters() throws Exception {   //SIM-157
        WebResponse rs = wc.getResponse( baseUrl + "/contentlength/page-decorator-none.jsp" );
        assertTrue(rs.getText().endsWith("</html>"));
    }

    public void testContentLengthSetCorrectlyWithNoneDecorator() throws Exception { 
        int contentLength = 10;
        WebResponse rs = wc.getResponse( baseUrl + "/contentlength/ContentLengthNoDecorator?content-length=" + contentLength);
        assertEquals("Content Length Header should equal " + contentLength, String.valueOf(contentLength) ,rs.getHeaderField("Content-Length"));
        assertEquals("Content Length should equal " + contentLength + " but was " + rs.getText().length(), contentLength, rs.getText().length());
    }
}
