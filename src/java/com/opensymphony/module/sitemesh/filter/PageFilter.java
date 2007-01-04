/* This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file. */
package com.opensymphony.module.sitemesh.filter;

import com.opensymphony.module.sitemesh.*;
import com.opensymphony.module.sitemesh.util.Container;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Main SiteMesh filter for applying Decorators to entire Pages.
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @author <a href="scott@atlassian.com">Scott Farquhar</a>
 * @version $Revision: 1.16 $
 */
public class PageFilter implements Filter, RequestConstants {
    protected FilterConfig filterConfig = null;
    protected Factory factory = null;

    /**
     * Main method of the Filter.
     *
     * <p>Checks if the Filter has been applied this request. If not, parses the page
     * and applies {@link com.opensymphony.module.sitemesh.Decorator} (if found).
     */
    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) rq;

        if (rq.getAttribute(FILTER_APPLIED) != null || factory.isPathExcluded(extractRequestPath(request))) {
            // ensure that filter is only applied once per request
            chain.doFilter(rq, rs);
        }
        else {
            request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

            factory.refresh();
            DecoratorMapper decoratorMapper = factory.getDecoratorMapper();

            HttpServletResponse response = (HttpServletResponse) rs;

            // parse data into Page object (or continue as normal if Page not parseable)
            Page page = parsePage(request, response, chain);

            if (page != null) {
                page.setRequest(request);

                Decorator decorator = decoratorMapper.getDecorator(request, page);
                if (decorator != null && decorator.getPage() != null) {
                    applyDecorator(page, decorator, request, response);
                    return;
                }

                // if we got here, an exception occured or the decorator was null,
                // what we don't want is an exception printed to the user, so
                // we write the original page
                writeOriginal(request, response, page);
            }
        }
    }

    private String extractRequestPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        String query = request.getQueryString();
        return (servletPath == null ? "" : servletPath)
                + (pathInfo == null ? "" : pathInfo)
                + (query == null ? "" : ("?" + query));
    }

    /** Set FilterConfig, and get instance of {@link com.opensymphony.module.sitemesh.DecoratorMapper}. */
    public void init(FilterConfig filterConfig) {
        if (filterConfig != null) {
            this.filterConfig = filterConfig;
            factory = Factory.getInstance(new Config(filterConfig));
        }
        else {
            destroy();
        }
    }

    /** @deprecated Not needed in final version of Servlet 2.3 API - replaced by init(). */
    // NOTE: SiteMesh doesn't work with Orion 1.5.2 without this method
    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    /** @deprecated Not needed in final version of Servlet 2.3 API - replaced by init(). */
    // NOTE: SiteMesh doesn't work with Orion 1.5.2 without this method
    public void setFilterConfig(FilterConfig filterConfig) {
        init(filterConfig);
    }

    /** Shutdown filter. */
    public void destroy() {
        factory = null;
    }

    /**
     * Continue in filter-chain, writing all content to buffer and parsing
     * into returned {@link com.opensymphony.module.sitemesh.Page} object. If
     * {@link com.opensymphony.module.sitemesh.Page} is not parseable, null is returned.
     */
    protected Page parsePage(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            PageResponseWrapper pageResponse = new PageResponseWrapper(response, factory);
            chain.doFilter(request, pageResponse);
            // check if another servlet or filter put a page object to the request
            Page result = (Page)request.getAttribute(PAGE);
            if (result == null) {
                // parse the page
                result = pageResponse.getPage();
            }
            request.setAttribute(USING_STREAM, pageResponse.isUsingStream() ? Boolean.TRUE : Boolean.FALSE); // JDK 1.3 friendly
            return result;
        }
        catch (IllegalStateException e) {
            // weblogic throws an IllegalStateException when an error page is served.
            // it's ok to ignore this, however for all other containers it should be thrown
            // properly.
            if (Container.get() != Container.WEBLOGIC) throw e;
            return null;
        }
    }

    /**
     * Apply {@link com.opensymphony.module.sitemesh.Decorator} to
     * {@link com.opensymphony.module.sitemesh.Page} and write to the response.
     */
    protected void applyDecorator(final Page page, Decorator decorator, HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute(PAGE, page);
            ServletContext context = filterConfig.getServletContext();
            // see if the URI path (webapp) is set
            if (decorator.getURIPath() != null) {
                // in a security conscious environment, the servlet container
                // may return null for a given URL
                if (context.getContext(decorator.getURIPath()) != null) {
                    context = context.getContext(decorator.getURIPath());
                }
            }
            // get the dispatcher for the decorator
            RequestDispatcher dispatcher = context.getRequestDispatcher(decorator.getPage());
            // create a wrapper around the response

            writeDecorator(response, page, dispatcher, request);

            request.removeAttribute(PAGE);
        }
        catch (RuntimeException e) {
            // added a print message here because otherwise Tomcat swallows
            // the error and you never see it = bad!
            if (Container.get() == Container.TOMCAT)
                e.printStackTrace();

            throw e;
        }
    }

    protected void writeDecorator(HttpServletResponse response, Page page, RequestDispatcher dispatcher, HttpServletRequest request) throws ServletException, IOException {
        dispatcher.include(request, response);
    }

    /** Write the original page data to the response. */
    private void writeOriginal(HttpServletRequest request, HttpServletResponse response, Page page) throws IOException {
        response.setContentLength(page.getContentLength());
        if (request.getAttribute(USING_STREAM).equals(Boolean.TRUE))
        {
            PrintWriter writer = new PrintWriter(response.getOutputStream());
            page.writePage(writer);
            //flush writer to underlying outputStream
            writer.flush();
            response.getOutputStream().flush();
        }
        else
        {
            page.writePage(response.getWriter());
            response.getWriter().flush();
        }
    }
}
