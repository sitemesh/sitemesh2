package com.opensymphony.sitemesh.webapp;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.module.sitemesh.scalability.ScalabilitySupport;
import com.opensymphony.module.sitemesh.scalability.ScalabilitySupportConfiguration;
import com.opensymphony.module.sitemesh.scalability.outputlength.MaxOutputLengthExceeded;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.Decorator;
import com.opensymphony.sitemesh.DecoratorSelector;
import com.opensymphony.sitemesh.ContentProcessor;
import com.opensymphony.sitemesh.compatability.DecoratorMapper2DecoratorSelector;
import com.opensymphony.sitemesh.compatability.PageParser2ContentProcessor;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Core Filter for integrating SiteMesh into a Java web application.
 *
 * @author Joe Walnes
 * @author Scott Farquhar
 * @since SiteMesh 3
 */
public class SiteMeshFilter implements Filter {

    private FilterConfig filterConfig;
    private ContainerTweaks containerTweaks;
    private ScalabilitySupportConfiguration scalabilitySupportConfiguration;
    private static final String ALREADY_APPLIED_KEY = "com.opensymphony.sitemesh.APPLIED_ONCE";

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        containerTweaks = new ContainerTweaks();
        scalabilitySupportConfiguration = new ScalabilitySupportConfiguration(filterConfig);
    }

    public void destroy() {
        filterConfig = null;
        containerTweaks = null;
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

        ContentProcessor contentProcessor = initContentProcessor(webAppContext);
        DecoratorSelector decoratorSelector = initDecoratorSelector(webAppContext);

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

        ScalabilitySupport scalabilitySupport = scalabilitySupportConfiguration.getScalabilitySupport(request);
        try {

            Content content = obtainContent(contentProcessor, webAppContext,scalabilitySupport,request, response, chain);

            if (content == null) {
                request.setAttribute(ALREADY_APPLIED_KEY, null);
                return;
            }

            Decorator decorator = decoratorSelector.selectDecorator(content, webAppContext);
            decorator.render(content, webAppContext);

        } catch (MaxOutputLengthExceeded exceeded) {
            //
            // they have sent a response that is bigger than is what acceptable so
            // we send back an HTTP code to indicate this
            handleMaximumExceeded(scalabilitySupport, request, response, servletContext, exceeded);

        } catch (IllegalStateException e) {
            // Some containers (such as WebLogic) throw an IllegalStateException when an error page is served.
            // It may be ok to ignore this. However, for safety it is propegated if possible.
            if (!containerTweaks.shouldIgnoreIllegalStateExceptionOnErrorPage()) {
                throw e;
            }
        } catch (RuntimeException e) {
            if (containerTweaks.shouldLogUnhandledExceptions()) {
                // Some containers (such as Tomcat 4) swallow RuntimeExceptions in filters.
                servletContext.log("Unhandled exception occurred whilst decorating page", e);
            }
            throw e;
        } catch (ServletException e) {
            request.setAttribute(ALREADY_APPLIED_KEY, null);
            if (e.getCause() instanceof MaxOutputLengthExceeded) {
                //
                // they have sent a response that is bigger than is what acceptable so
                // we send back an HTTP code to indicate this
                handleMaximumExceeded(scalabilitySupport, request, response, servletContext, (MaxOutputLengthExceeded) e.getCause());
            } else {
                throw e;
            }
        } finally {
            cleanupSecondaryStorage(scalabilitySupport.getSecondaryStorage(), servletContext);
        }

    }

    private void handleMaximumExceeded(ScalabilitySupport scalabilitySupport, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, MaxOutputLengthExceeded exceeded) throws IOException
    {
        request.setAttribute(RequestConstants.MAXIMUM_OUTPUT_EXCEEDED_LENGTH, exceeded.getMaxOutputLength());
        if (scalabilitySupport.isMaxOutputLengthExceededThrown())
        {
            throw exceeded;
        }
        else
        {
            servletContext.log("Exceeded the maximum SiteMesh page output size", exceeded);
            response.sendError(exceeded.getMaximumOutputExceededHttpCode(), exceeded.getMessage());
        }
    }

    private void cleanupSecondaryStorage(SecondaryStorage secondaryStorage, ServletContext servletContext)
    {
        // we want to cleanup without exception.  The request may already be in exception and we don't want to make
        // make it any worse.  Also we have told the implementer NOT to propagate an exception and hence we are going to
        // hold them to that contract
        try
        {
            secondaryStorage.cleanUp();
        }
        catch (Exception e)
        {
            servletContext.log("Unable to clean up secondary storage properly.  Ignoring exception ", e);
        }
    }

    protected ContentProcessor initContentProcessor(SiteMeshWebAppContext webAppContext) {
        // TODO: Remove heavy coupling on horrible SM2 Factory
        Factory factory = Factory.getInstance(new Config(filterConfig));
        factory.refresh();
        return new PageParser2ContentProcessor(factory);
    }

    protected DecoratorSelector initDecoratorSelector(SiteMeshWebAppContext webAppContext) {
        // TODO: Remove heavy coupling on horrible SM2 Factory
        Factory factory = Factory.getInstance(new Config(filterConfig));
        factory.refresh();
        return new DecoratorMapper2DecoratorSelector(factory.getDecoratorMapper());
    }

    /**
     * Continue in filter-chain, writing all content to buffer and parsing
     * into returned {@link com.opensymphony.module.sitemesh.Page} object. If
     * {@link com.opensymphony.module.sitemesh.Page} is not parseable, null is returned.
     */
    private Content obtainContent(ContentProcessor contentProcessor, SiteMeshWebAppContext webAppContext, ScalabilitySupport scalabilitySupport,
                                  HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        ContentBufferingResponse contentBufferingResponse = new ContentBufferingResponse(response, request, contentProcessor, webAppContext, scalabilitySupport);
        chain.doFilter(request, contentBufferingResponse);
        webAppContext.setUsingStream(contentBufferingResponse.isUsingStream());
        return contentBufferingResponse.getContent();
    }

    private boolean filterAlreadyAppliedForRequest(HttpServletRequest request) {
        if (request.getAttribute(ALREADY_APPLIED_KEY) == Boolean.TRUE) {
            return true;
        } else {
            request.setAttribute(ALREADY_APPLIED_KEY, Boolean.TRUE);
            return false;
        }
    }

}
