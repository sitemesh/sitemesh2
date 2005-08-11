package com.opensymphony.sitemesh.webapp;

import com.opensymphony.module.sitemesh.util.Container;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.Decorator;
import com.opensymphony.sitemesh.DecoratorSelector;
import com.opensymphony.sitemesh.content.ContentProcessor;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Core Filter for integrating SiteMesh into a Java web application.
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @author <a href="scott@atlassian.com">Scott Farquhar</a>
 */
public class SiteMeshFilter implements Filter {

    private FilterConfig filterConfig;
    private ContainerTweaks containerTweaks = new ContainerTweaks();

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    public void destroy() {
        filterConfig = null;
    }

    /**
     * Main method of the Filter.
     * <p>Checks if the Filter has been applied this request. If not, parses the page
     * and applies {@link com.opensymphony.module.sitemesh.Decorator} (if found).
     */
    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) rq;
        HttpServletResponse response = (HttpServletResponse) rs;
        ServletContext servletContext = filterConfig.getServletContext();

        SiteMeshWebAppContext webAppContext = new SiteMeshWebAppContext(request, response, servletContext);
        ContentProcessor contentProcessor = null; // TODO
        DecoratorSelector decoratorSelector = null; // TODO

        if (filterAlreadyAppliedForRequest(request)) {
            // Prior to Servlet 2.4 spec, it was unspecified whether the filter should be called again upon an include().
            chain.doFilter(request, response);
            return;
        }

        if (!contentProcessor.handles(webAppContext)) {
            // Optimization: If the content doesn't need to be processed, bypass SiteMesh.
            chain.doFilter(request, response);
            return;
        }

        if (containerTweaks.shouldAutoCreateSession()) {
            // Some containers (such as Tomcat 4) will not allow sessions to be created in the decorator.
            // (i.e after the response has been committed).
            request.getSession(true);
        }

        try {

            Content content = obtainContent(contentProcessor, webAppContext, request, response, chain);

            if (content == null) {
                return;
            }

            Decorator decorator = decoratorSelector.selectDecorator(content, webAppContext);
            decorator.render(content, webAppContext);

        } catch (RuntimeException e) {
            if (containerTweaks.shouldLogUnhandledExceptions()) {
                // Some containers (such as Tomcat 4) swallow RuntimeExceptions in filters.
                servletContext.log("Unhandled exception occurred whilst decorating page", e);
            }
            throw e;
        }

    }

    /**
     * Continue in filter-chain, writing all content to buffer and parsing
     * into returned {@link com.opensymphony.module.sitemesh.Page} object. If
     * {@link com.opensymphony.module.sitemesh.Page} is not parseable, null is returned.
     */
    private Content obtainContent(ContentProcessor contentProcessor, SiteMeshWebAppContext webAppContext,
                                  HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            ContentBufferingResponse contentBufferingResponse = new ContentBufferingResponse(response, contentProcessor);
            chain.doFilter(request, contentBufferingResponse);
            // TODO: check if another servlet or filter put a page object to the request
            //            Content result = request.getAttribute(PAGE);
            //            if (result == null) {
            //                // parse the page
            //                result = pageResponse.getPage();
            //            }
            webAppContext.setUsingStream(contentBufferingResponse.isUsingStream());
            return contentBufferingResponse.getContent();
        }
        catch (IllegalStateException e) {
            // weblogic throws an IllegalStateException when an error page is served.
            // it's ok to ignore this, however for all other containers it should be thrown
            // properly.
            if (Container.get() != Container.WEBLOGIC) throw e;
            return null;
        }
    }

    private boolean filterAlreadyAppliedForRequest(HttpServletRequest request) {
        final String alreadyAppliedKey = "com.opensymphony.sitemesh.APPLIED_ONCE";
        if (request.getAttribute(alreadyAppliedKey) == Boolean.TRUE) {
            return true;
        } else {
            request.setAttribute(alreadyAppliedKey, Boolean.TRUE);
            return false;
        }
    }

}
