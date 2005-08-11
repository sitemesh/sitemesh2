package com.opensymphony.sitemesh.webapp;

import com.opensymphony.module.sitemesh.filter.PageResponseWrapper;
import com.opensymphony.module.sitemesh.PageParserSelector;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.sitemesh.content.ContentProcessor;
import com.opensymphony.sitemesh.Content;

import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ContentBufferingResponse extends HttpServletResponseWrapper {

    // NOTE: Temporary SM3 migration implementation! Wraps SM2 PageResponseWrapper.
    // Eventually PageResponseWrapper will go away and the functionality will be rolled into this class.

    private final PageResponseWrapper pageResponseWrapper;
    private final ContentProcessor contentProcessor;

    public ContentBufferingResponse(HttpServletResponse response, final ContentProcessor contentProcessor) {
        super(new PageResponseWrapper(response, new PageParserSelector() {
            public boolean shouldParsePage(String contentType) {
                return contentProcessor.handles(contentType);
            }

            public PageParser getPageParser(String contentType) {
                // Migration: Not actually needed by PageResponseWrapper, so long as getPage() isn't called.
                throw new UnsupportedOperationException();
            }
        }));
        this.contentProcessor = contentProcessor;
        pageResponseWrapper = (PageResponseWrapper) getResponse();
    }

    public boolean isUsingStream() {
        return pageResponseWrapper.isUsingStream();
    }

    public Content getContent() throws IOException {
        char[] data = pageResponseWrapper.getContents();
        if (data != null) {
            return contentProcessor.build(data);
        } else {
            return null;
        }
    }
}
