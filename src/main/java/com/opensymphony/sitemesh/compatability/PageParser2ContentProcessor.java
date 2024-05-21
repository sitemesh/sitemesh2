package com.opensymphony.sitemesh.compatability;

import com.opensymphony.module.sitemesh.*;
import com.opensymphony.module.sitemesh.filter.HttpContentType;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.SiteMeshContext;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;
import com.opensymphony.sitemesh.ContentProcessor;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Adapts a SiteMesh 2 {@link PageParser} to a SiteMesh 3 {@link ContentProcessor}.
 *
 * @author Joe Walnes
 * @since SiteMesh 3
 */
public class PageParser2ContentProcessor implements ContentProcessor {

    private final Factory factory;

    public PageParser2ContentProcessor(Factory factory) {
        this.factory = factory;
    }

    public boolean handles(SiteMeshContext context) {
        SiteMeshWebAppContext webAppContext = (SiteMeshWebAppContext) context;
        return !factory.isPathExcluded(extractRequestPath(webAppContext.getRequest()));
    }

    private String extractRequestPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        String query = request.getQueryString();
        return (servletPath == null ? "" : servletPath)
                + (pathInfo == null ? "" : pathInfo)
                + (query == null ? "" : ("?" + query));
    }

    public boolean handles(String contentType) {
        return factory.shouldParsePage(contentType);
    }

    public Content build(SitemeshBuffer buffer, SiteMeshContext context) throws IOException {
        HttpContentType httpContentType = new HttpContentType(context.getContentType());
        PageParser pageParser = factory.getPageParser(httpContentType.getType());
        Page page = pageParser.parse(buffer);
        return new HTMLPage2Content((HTMLPage) page);
    }
}
