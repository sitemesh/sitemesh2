package com.opensymphony.module.sitemesh.multipass;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.PageParserSelector;
import com.opensymphony.sitemesh.webapp.SiteMeshFilter;
import com.opensymphony.module.sitemesh.filter.PageResponseWrapper;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MultipassFilter extends SiteMeshFilter {

    protected void writeDecorator(final HttpServletResponse response, final Page page, RequestDispatcher dispatcher, HttpServletRequest request) throws ServletException, IOException {
        PageResponseWrapper pageResponse = new PageResponseWrapper(response, new PageParserSelector() {
            public boolean shouldParsePage(String contentType) {
                return true;
            }

            public PageParser getPageParser(String contentType) {
                return new MultipassReplacementPageParser(page, response);
            }
        });
        pageResponse.activateSiteMesh("text/html", "");
        dispatcher.include(request, pageResponse);
        pageResponse.getPage();
    }

}
