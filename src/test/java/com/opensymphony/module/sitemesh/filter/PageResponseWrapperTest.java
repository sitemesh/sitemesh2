package com.opensymphony.module.sitemesh.filter;

import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.PageParserSelector;
import com.opensymphony.module.sitemesh.RequestConstants;
import junit.framework.TestCase;
import org.mockito.internal.stubbing.answers.ThrowsException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

public class PageResponseWrapperTest extends TestCase {
    public void testSitmeshDisabledByRequestAttribute() throws Exception {
        PageParser parser = mock(PageParser.class, new ThrowsException(new RuntimeException("should not be called")));
        PageParserSelector selector = mock(PageParserSelector.class);
        when(selector.shouldParsePage("text/html")).thenReturn(true);
        when(selector.getPageParser("text/html")).thenReturn(parser);


        HttpServletRequest req = mock(HttpServletRequest.class);

        HttpServletResponse origRes = mock(HttpServletResponse.class);
        final StringWriter outbuf = new StringWriter();
        when(origRes.getWriter()).thenReturn(new PrintWriter(outbuf));

        PageResponseWrapper wrapper = new PageResponseWrapper(origRes, req, selector);


        wrapper.setContentType("text/html");
        assertTrue(wrapper.isParseablePage()); // sitmesh is ready to decorate it
        Buffer originalBuffer = wrapper.getBuffer();
        assertNotNull(originalBuffer);
        assertFalse(originalBuffer.hasBeenOpened());
        assertFalse(wrapper.isAborted());

        // set the request attribute just before getting the writer:
        when(req.getAttribute(RequestConstants.DISABLE_BUFFER_AND_DECORATION)).thenReturn(true);
        wrapper.getWriter().write("helloworld");

        assertFalse(wrapper.isParseablePage());
        assertNull(wrapper.getBuffer());
        assertFalse(wrapper.isAborted());
        assertFalse(originalBuffer.hasBeenOpened()); // still hasn't been opened yet

        verify(origRes).setContentType("text/html");
        assertEquals("helloworld", outbuf.toString());

    }
}
