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
 * @version $Revision: 1.1 $
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


    // this will one day be used to cache parsed pages.
    private long lastModified;

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
        int index = -1;

        if (type != null) {
            if ((index = type.lastIndexOf("charset=")) != -1) {
                encoding = type.substring(index + 8).trim();
            }

            if ((index = type.indexOf(';')) > 0) {
                contentType = type.substring(0, index);
            } else {
                contentType = type;
            }

            // this is the content type + charset. eg: text/html;charset=utf-8
            super.setContentType(type);
        }

        if (Factory.getInstance(config).shouldParsePage(contentType)) {
            parseablePage = true;
        } else {
            getBufferStream().discardBuffer();
            parseablePage = false;
        }
    }

    /** Prevent content-length being set if page is parseable. */
    public void setContentLength(int contentLength) {
        if (!parseablePage()) super.setContentLength(contentLength);
    }

    /** Prevent Last-Modified header being set if page is parseable. */
    public void setDateHeader(String name, long value) {
        if (parseablePage() && "Last-Modified".equalsIgnoreCase(name)) {
            lastModified = value;
        } else {
            super.setDateHeader(name, value);
        }
    }

    /**
     * Prevent 'not modified' (304) HTTP status from being sent if page is parseable
     * (so web-server/browser doesn't cache contents).
     */
    public void setStatus(int sc) {
        if (sc == HttpServletResponse.SC_NOT_MODIFIED) {
            if (!parseablePage()) super.setStatus(sc);
        } else {
            super.setStatus(sc);
        }
    }

    /**
     * If the server is Orion, Resin or Tomcat, return a wrapped ServletOutputStream, else return the
     * default ServletOutputStream.
     *
     * <p>This is called internally by Orion 1.5.4, Resin 2.1.0, Tomcat 4.1.12 - naughty!</p>
     *
     * @throws IOException
     */
    public ServletOutputStream getOutputStream() throws IOException {
        return getPageOutputStream();

    }

    private PageOutputStream getPageOutputStream() throws IOException {
        if (pageOutputStream == null) {
            if (usingWriter) {
                throw new IllegalStateException("Cannot call getOutputStream() after getWriter()");
            } else {
                usingStream = true;
                pageOutputStream = new PageOutputStream(super.getOutputStream(), encoding);
            }
        }
        return pageOutputStream;
    }

    /**
     * Return instance of {@link com.opensymphony.module.sitemesh.filter.PageWriter}
     * allowing all data written to stream to be stored in temporary buffer.
     */
    public PrintWriter getWriter() throws IOException {
        if (debug) {
            return new DebugPageWriter(getPageWriter()); // verbose debugging of PageWriter
        }
        return getPageWriter();
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
    public void closeWriter() throws IOException {
        if (parseablePage()) {
            getBufferStream().flush();
        }
    }

    /** Determine whether to contents of this request are parseable by SiteMesh. */
    private boolean parseablePage() {
        return parseablePage;
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
     *
     * @see #parseablePage()
     */
    public Page getPage() throws IOException {
        getBufferStream().flush();
        Factory factory = Factory.getInstance(config);
        if (contentType == null || !factory.shouldParsePage(contentType)) {
            // just in case setContentType was never called, or called before content was written to output
            getBufferStream().discardBuffer();
        }
        if (aborted || !parseablePage()) return null;
        if (page == null) {
            // @todo: the lastModified field is available, so the parsed-page could be retrieved from
            //        a cache.
            PageParser parser = factory.getPageParser(contentType);
            return parser.parse(getBufferStream().getBuffer());
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