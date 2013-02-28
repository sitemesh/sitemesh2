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
        assertTrue("Text does not end with '</html>'.  Full text:" + rs.getText(), rs.getText().endsWith("</html>"));
    }

    public void testContentLengthSetCorrectlyWithNoneDecorator() throws Exception { 
        int contentLength = 10;
        WebResponse rs = wc.getResponse( baseUrl + "/contentlength/ContentLengthNoDecorator?content-length=" + contentLength);
        // we don't send content-length for none any more, see JRADEV-6607 and commit:4fcde51c60dfa43b60884291cb68e70851255b94
//        assertEquals("Content Length Header should equal " + contentLength, String.valueOf(contentLength) ,rs.getHeaderField("Content-Length"));
        assertEquals("Content Length should equal " + contentLength + " but was " + rs.getText().length(), contentLength, rs.getText().length());
    }

    // SIM-217 - test that content length with decorators is set correctly.
    public void testContentLengthSetCorrectlyWithSimpleDecorator() throws Exception {
        int decoratedContentLen = 10;
        WebResponse rs = wc.getResponse( baseUrl + "/ForwardServlet?target=/contentlength/ContentLengthWithDecorator&content-length=" + decoratedContentLen);

        /*
         * XXX: If the simple decorator is modified the following value will need
         * to be reset. To figure out what the length of the decorator, set the decoratedContentLen to 0
         * and run the test and use the value that gets spit out.
         */
        int simpleDecoratorLen = 387;
        int expectedLength = simpleDecoratorLen + decoratedContentLen;

        System.out.println("rs.getText() = " + rs.getText());
        assertEquals("Content Length should equal " + expectedLength + " but was " + rs.getText().length(), expectedLength, rs.getText().length());
        String contentLengthHeader = rs.getHeaderField("Content-Length");
        if (contentLengthHeader != null) // not setting the content length header is fine as well
            assertEquals("Content Length Header should equal " + expectedLength, String.valueOf(expectedLength), contentLengthHeader);
     }

}
