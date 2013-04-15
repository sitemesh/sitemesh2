/* This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file. */
package com.opensymphony.module.sitemesh.filter;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParserSelector;
import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.scalability.NoopScalabilitySupport;
import com.opensymphony.module.sitemesh.scalability.ScalabilitySupport;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Implementation of HttpServletResponseWrapper that captures page data instead of
 * sending to the writer.
 * <p/>
 * <p>Should be used in filter-chains or when forwarding/including pages
 * using a RequestDispatcher.</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
 * @version $Revision: 1.17 $
 */
public class PageResponseWrapper extends HttpServletResponseWrapper {

    private final RoutablePrintWriter routablePrintWriter;
    private final RoutableServletOutputStream routableServletOutputStream;
    private final PageParserSelector parserSelector;

    private Buffer buffer;
    private boolean aborted = false;
    private boolean parseablePage = false;
    private final ScalabilitySupport scalabilitySupport;
    private final HttpServletRequest request;

    public PageResponseWrapper(final HttpServletResponse response, final PageParserSelector parserSelector) {
        this(response, null, new NoopScalabilitySupport(), parserSelector);
    }

    public PageResponseWrapper(final HttpServletResponse response, final HttpServletRequest request, final PageParserSelector parserSelector) {
        this(response, request, new NoopScalabilitySupport(), parserSelector);
    }

    public PageResponseWrapper(final HttpServletResponse response, final ScalabilitySupport scalabilitySupport, final PageParserSelector parserSelector) {
        this(response, null, scalabilitySupport, parserSelector);
    }

    public PageResponseWrapper(final HttpServletResponse response, final HttpServletRequest request, final ScalabilitySupport scalabilitySupport, final PageParserSelector parserSelector) {
        super(response);
        this.request = request;
        this.scalabilitySupport = scalabilitySupport;
        this.parserSelector = parserSelector;

        routablePrintWriter = new RoutablePrintWriter(new RoutablePrintWriter.DestinationFactory() {
            public PrintWriter activateDestination() throws IOException {
                return response.getWriter();
            }
        });
        routableServletOutputStream = new RoutableServletOutputStream(new RoutableServletOutputStream.DestinationFactory() {
            public ServletOutputStream create() throws IOException {
                return response.getOutputStream();
            }
        });

    }

    /**
     * Set the content-type of the request and store it so it can
     * be passed to the {@link com.opensymphony.module.sitemesh.PageParser}.
     */
    public void setContentType(String type) {
        super.setContentType(type);

        if (type != null) {
            HttpContentType httpContentType = new HttpContentType(type);

            if (parserSelector.shouldParsePage(httpContentType.getType())) {
                activateSiteMesh(httpContentType.getType(), httpContentType.getEncoding());
            } else {
                deactivateSiteMesh();
            }
        }

    }

    public void activateSiteMesh(String contentType, String encoding) {
        if (parseablePage) {
            return; // already activated
        }
        buffer = new Buffer(parserSelector.getPageParser(contentType), encoding, scalabilitySupport);
        routablePrintWriter.updateDestination(new RoutablePrintWriter.DestinationFactory() {
            public PrintWriter activateDestination() throws IOException {
                return lazyDisable() ? getResponse().getWriter() : buffer.getWriter();
            }
        });
        routableServletOutputStream.updateDestination(new RoutableServletOutputStream.DestinationFactory() {
            public ServletOutputStream create() throws IOException {
                return lazyDisable() ? getResponse().getOutputStream() : buffer.getOutputStream();
            }
        });
        parseablePage = true;
    }

    private boolean lazyDisable() {
        if (null != request && request.getAttribute(RequestConstants.DISABLE_BUFFER_AND_DECORATION) != null) {
            parseablePage = false;
            buffer = null;
            return true;
        } else {
            return false;
        }
    }

    private void deactivateSiteMesh() {
        parseablePage = false;
        buffer = null;
        routablePrintWriter.updateDestination(new RoutablePrintWriter.DestinationFactory() {
            public PrintWriter activateDestination() throws IOException {
                return getResponse().getWriter();
            }
        });
        routableServletOutputStream.updateDestination(new RoutableServletOutputStream.DestinationFactory() {
            public ServletOutputStream create() throws IOException {
                return getResponse().getOutputStream();
            }
        });
    }

    /**
     * Prevent content-length being set if page is parseable.
     */
    public void setContentLength(int contentLength) {
        if (!parseablePage) super.setContentLength(contentLength);
    }

    /**
     * Prevent buffer from being flushed if this is a page being parsed.
     */
    public void flushBuffer() throws IOException {
        if (!parseablePage) super.flushBuffer();
    }

    /**
     * Prevent content-length being set if page is parseable.
     */
    public void setHeader(String name, String value) {
        if (name.toLowerCase().equals("content-type")) { // ensure ContentType is always set through setContentType()
            setContentType(value);
        } else if (!parseablePage || !name.toLowerCase().equals("content-length")) {
            super.setHeader(name, value);
        }
    }

    /**
     * Prevent content-length being set if page is parseable.
     */
    public void addHeader(String name, String value) {
        if (name.toLowerCase().equals("content-type")) { // ensure ContentType is always set through setContentType()
            setContentType(value);
        } else if (!parseablePage || !name.toLowerCase().equals("content-length")) {
            super.addHeader(name, value);
        }
    }

    /**
     * If 'not modified' (304) HTTP status is being sent - then abort parsing, as there shouldn't be any body
     */
    public void setStatus(int sc) {
        if (sc == HttpServletResponse.SC_NOT_MODIFIED) {
            aborted = true;
            // route any content back to the original writer.  There shouldn't be any content, but just to be safe
            deactivateSiteMesh();
        }
        super.setStatus(sc);
    }

    public ServletOutputStream getOutputStream() {
        return routableServletOutputStream;
    }

    public PrintWriter getWriter() {
        return routablePrintWriter;
    }

    public Page getPage() throws IOException {
        if (aborted || !parseablePage) {
            return null;
        } else {
            return buffer.parse();
        }
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

    public boolean isUsingStream() {
        return buffer != null && buffer.isUsingStream();
    }

    public SitemeshBuffer getContents() throws IOException {
        if (aborted || !parseablePage) {
            return null;
        } else {
            return buffer.getContents();
        }
    }

    public Buffer getBuffer() {
        return buffer;
    }

    public boolean isAborted() {
        return aborted;
    }

    public boolean isParseablePage() {
        return parseablePage;
    }
}