/* This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file. */
package com.opensymphony.module.sitemesh.filter;

import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.module.sitemesh.Page;

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
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
 * @version $Revision: 1.12 $
 */
public final class PageResponseWrapper extends HttpServletResponseWrapper {

    private final RoutablePrintWriter routablePrintWriter;
    private final RoutableServletOutputStream routableServletOutputStream;
    private final Factory factory;

    private Buffer buffer;
    private boolean aborted = false;
    private boolean parseablePage = false;

    public PageResponseWrapper(final HttpServletResponse response, Factory factory) {
        super(response);
        this.factory = factory;

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
            // this is the content type + charset. eg: text/html;charset=UTF-8
            int offset = type.lastIndexOf("charset=");
            String encoding = null;
            if (offset != -1)
               encoding = extractContentTypeValue(type, offset + 8);
            String contentType = extractContentTypeValue(type, 0);

            if (factory.shouldParsePage(contentType)) {
                activateSiteMesh(contentType, encoding);
            }
        }

    }

    private void activateSiteMesh(String contentType, String encoding) {
        if (parseablePage) {
            return; // already activated
        }
        parseablePage = true;
        buffer = new Buffer(factory, contentType, encoding);
        routablePrintWriter.updateDestination(new RoutablePrintWriter.DestinationFactory() {
            public PrintWriter activateDestination() {
                return buffer.getWriter();
            }
        });
        routableServletOutputStream.updateDestination(new RoutableServletOutputStream.DestinationFactory() {
            public ServletOutputStream create() {
                return buffer.getOutputStream();
            }
        });
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
        if (name.toLowerCase().equals("content-type")) { // ensure ContentType is always set through setContentType()
            setContentType(value);
        } else if (!parseablePage || !name.toLowerCase().equals("content-length")) {
            super.setHeader(name, value);
        }
    }

    /** Prevent content-length being set if page is parseable. */
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
}