/*
 * Title:        PageResponseWrapper
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.filter;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Implementation of HttpServletResponseWrapper that captures page data instead of
 * sending to the writer.
 *
 * <p>Should be used in filter-chains or when forwarding/including pages
 * using a RequestDispatcher.</p>
 *
 * <p>In order to capture the response, {@link #getWriter()} returns
 * an instance of {@link com.opensymphony.module.sitemesh.filter.PageWriter}.</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
 * @version $Revision: 1.8 $
 */
public final class PageResponseWrapper extends HttpServletResponseWrapper {


    private PageWriter pageWriter = null;
    private PageOutputStream pageOutputStream = null;

    private boolean usingStream = false;
    private boolean usingWriter = false;

    private Config config = null;
    private Page page = null;

    private String contentType = null;
    private String encoding = null;

    private boolean aborted = false;
    private boolean parseablePage = false;
    private boolean setContentTypeCalled = false;

    // verbose debugging of PageWriter
    private boolean debug;

    private HttpServletResponse response = null;

    /** Create PageResponse wrapped around an existing HttpServletResponse. */
    public PageResponseWrapper(HttpServletResponse response, Config config) throws IOException {
        this(response, config, false);
    }

    /** Create PageResponse wrapped around an existing HttpServletResponse. */
    public PageResponseWrapper(HttpServletResponse response, Config config, boolean debug) throws IOException {
        super(response);
        this.response = response;
        this.config = config;
        this.debug = debug;
    }

    /**
     * Set the content-type of the request and store it so it can
     * be passed to the {@link com.opensymphony.module.sitemesh.PageParser}.
     */
    public void setContentType(String type) {
        setContentTypeCalled = true;
        if (type != null) {
            int offset = type.lastIndexOf("charset=");
            if (offset != -1)
               encoding = extractContentTypeValue(type, offset + 8);
            contentType = extractContentTypeValue(type, 0);

            // this is the content type + charset. eg: text/html;charset=UTF-8
            super.setContentType(type);
        }

        if (Factory.getInstance(config).shouldParsePage(contentType)) {
            parseablePage = true;
        } else {
            getBufferStream().discardBuffer();
            parseablePage = false;
        }
    }

    private String extractContentTypeValue(String type, int startIndex) {
        if (startIndex < 0)
            return null;

        // Skip over any leading spaces
        while (startIndex < type.length() && type.charAt(startIndex) == ' ')
            startIndex++;

        if (startIndex >= type.length())
            return null;

        int endIndex = startIndex;

        if (type.charAt(startIndex) == '"') {
            startIndex++;
            endIndex = type.indexOf('"', startIndex);
            if (endIndex == -1)
                endIndex = type.length();
        } else {
            // Scan through until we hit either  the end of the string or a
            // special character (as defined in RFC-2045). Note that we ignore '/'
            // since we want to capture it as part of the value.
            char ch;
            while (endIndex < type.length() && (ch = type.charAt(endIndex)) != ' ' && ch != ';'
                  && ch != '(' && ch != ')' && ch != '[' && ch != ']' && ch != '<' && ch != '>'
                  && ch != ':' && ch != ',' && ch != '=' && ch != '?' && ch != '@' && ch!= '"'
                  && ch !='\\')
               endIndex++;
        }
        return type.substring(startIndex, endIndex);
    }

    /** Prevent content-length being set if page is parseable. */
    public void setContentLength(int contentLength) {
        if (!parseablePage) super.setContentLength(contentLength);
    }

    /** Prevent content-length being set if page is parseable. */
    public void setHeader(String name, String value) {
        if (!parseablePage || !name.toLowerCase().equals("content-length")) {
            super.setHeader(name, value);
        }
    }

    /**
     * Prevent 'not modified' (304) HTTP status from being sent if page is parseable
     * (so web-server/browser doesn't cache contents).
     */
    public void setStatus(int sc) {
        if (!parseablePage || sc != HttpServletResponse.SC_NOT_MODIFIED) {
            super.setStatus(sc);
        }
    }

    /**
     * <p>If the page is parseable, return a wrapped ServletOutputStream, else return the
     * default ServletOutputStream.</p>
     *
     * <p>This is called internally by Orion 1.5.4, Resin 2.1.0, Tomcat 4.1.12 - naughty!</p>
     *
     * @throws IOException
     */
    public ServletOutputStream getOutputStream() throws IOException {
        // If setContentType hasn't been called (which it should have by now)
        // we'll have to capture the page regardless
        parseablePage |= !setContentTypeCalled;

        if (parseablePage)
            return getPageOutputStream();
        else
            return super.getOutputStream();
    }

    private PageOutputStream getPageOutputStream() {
        if (pageOutputStream == null) {
            if (usingWriter) {
                throw new IllegalStateException("Cannot call getOutputStream() after getWriter()");
            } else {
                usingStream = true;
                pageOutputStream = new PageOutputStream(response);
            }
        }
        return pageOutputStream;
    }

    /**
     * <p>If the page is parseable we return an instance of
     * {@link com.opensymphony.module.sitemesh.filter.PageWriter} allowing all
     * data written to stream to be stored in temporary buffer.</p>
     */
    public PrintWriter getWriter() throws IOException {
       // If setContentType hasn't been called (which it should have by now)
       // we'll have to capture the page regardless
       parseablePage |= !setContentTypeCalled;

       if (parseablePage) {
            if (debug) {
                return new DebugPageWriter(getPageWriter()); // verbose debugging of PageWriter
            }
            return getPageWriter();
        } else {
            return super.getWriter();
        }
    }

    /**
     * Return instance of {@link com.opensymphony.module.sitemesh.filter.PageWriter}
     * allowing all data written to stream to be stored in temporary buffer.
     */
    private PageWriter getPageWriter() throws IOException {
        if (pageWriter == null) {
            if (usingStream) {
                throw new IllegalStateException("Cannot call getWriter() after getOutputStream()");
            } else {
                usingWriter = true;
                pageWriter = new PageWriter(response.getWriter());
            }
        }
        return pageWriter;
    }

    /** Flush and close output stream of wrapped response. */
    public void closeWriter() {
        if (parseablePage) {
            getBufferStream().flush();
        }
    }

    private OutputBuffer getBufferStream() {
        if (usingStream)
            return pageOutputStream;
        else if (usingWriter)
            return pageWriter;
        else
            return NullOutputBuffer.getInstance();
    }

    public boolean isUsingStream() {
        return usingStream;
    }

    public boolean isUsingWriter() {
        return usingWriter;
    }

    /**
     * Send data written to {@link com.opensymphony.module.sitemesh.filter.PageWriter}
     * to {@link com.opensymphony.module.sitemesh.PageParser} and return a
     * {@link com.opensymphony.module.sitemesh.Page} instance. If the
     * {@link com.opensymphony.module.sitemesh.Page} is not parseable,
     * null will be returned.
     */
    public Page getPage() throws IOException {
        getBufferStream().flush();
        Factory factory = Factory.getInstance(config);
        if (!parseablePage) {
            // just in case setContentType was never called, or called before content was written to output
            getBufferStream().discardBuffer();
            return null;
        }
        if (aborted)
            return null;

        if (page == null) {
            PageParser parser = factory.getPageParser(contentType);
            return parser.parse(getBufferStream().getBuffer(encoding));
        }
        return page;
    }

    public void sendError(int sc) throws IOException {
        aborted = true;
        super.sendError(sc);
    }

    public void sendError(int sc, String msg) throws IOException {
        aborted = true;
        super.sendError(sc, msg);
    }

    public void sendRedirect(String location) throws IOException {
        aborted = true;
        super.sendRedirect(location);
    }
}