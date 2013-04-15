package com.opensymphony.sitemesh.webapp;

import com.opensymphony.module.sitemesh.scalability.ScalabilitySupport;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.filter.PageResponseWrapper;
import com.opensymphony.module.sitemesh.PageParserSelector;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.sitemesh.ContentProcessor;
import com.opensymphony.sitemesh.Content;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Joe Walnes
 * @since SiteMesh 3
 */
public class ContentBufferingResponse extends HttpServletResponseWrapper {

    // TODO: Temporary SM3 migration implementation! Wraps SM2 PageResponseWrapper. This class is an evil stopgap.
    // Eventually PageResponseWrapper will go away and the functionality will be rolled into this class.

    private final PageResponseWrapper pageResponseWrapper;
    private final ContentProcessor contentProcessor;
    private final SiteMeshWebAppContext webAppContext;

    public ContentBufferingResponse(HttpServletResponse response, final ContentProcessor contentProcessor, final SiteMeshWebAppContext webAppContext, final ScalabilitySupport scalabilitySupport) {
        this(response, null, contentProcessor, webAppContext, scalabilitySupport);
    }

    public ContentBufferingResponse(HttpServletResponse response, HttpServletRequest request, final ContentProcessor contentProcessor, final SiteMeshWebAppContext webAppContext, final ScalabilitySupport scalabilitySupport) {
        super(new PageResponseWrapper(response, request,  scalabilitySupport, new PageParserSelector() {
            public boolean shouldParsePage(String contentType) {
                return contentProcessor.handles(contentType);
            }

            public PageParser getPageParser(String contentType) {
                // Migration: Not actually needed by PageResponseWrapper, so long as getPage() isn't called.
                return null;
            }
        }){
            public void setContentType(String contentType) {
                webAppContext.setContentType(contentType);
                super.setContentType(contentType);
            }
        });
        this.contentProcessor = contentProcessor;
        this.webAppContext = webAppContext;
        pageResponseWrapper = (PageResponseWrapper) getResponse();

        // We can't guarantee that response.setContentType will not be
        // called before this constructor, so we must check the type now.
        String existingContentType = response.getContentType();
        if (existingContentType != null)
        {
            pageResponseWrapper.setContentType(existingContentType);
        }
    }

    public boolean isUsingStream() {
        return pageResponseWrapper.isUsingStream();
    }

    public Content getContent() throws IOException {
        SitemeshBuffer content = pageResponseWrapper.getContents();
        if (content != null) {
            return contentProcessor.build(content, webAppContext);
        } else {
            return null;
        }
    }
}
