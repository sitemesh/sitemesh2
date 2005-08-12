package com.opensymphony.sitemesh.webapp;

import com.opensymphony.module.sitemesh.filter.PageResponseWrapper;
import com.opensymphony.module.sitemesh.PageParserSelector;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.sitemesh.ContentProcessor;
import com.opensymphony.sitemesh.Content;

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

    public ContentBufferingResponse(HttpServletResponse response, final ContentProcessor contentProcessor, final SiteMeshWebAppContext webAppContext) {
        super(new PageResponseWrapper(response, new PageParserSelector() {
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
    }

    public boolean isUsingStream() {
        return pageResponseWrapper.isUsingStream();
    }

    public Content getContent() throws IOException {
        char[] data = pageResponseWrapper.getContents();
        if (data != null) {
            return contentProcessor.build(data, webAppContext);
        } else {
            return null;
        }
    }
}
