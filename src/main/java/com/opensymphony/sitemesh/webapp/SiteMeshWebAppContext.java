package com.opensymphony.sitemesh.webapp;

import com.opensymphony.sitemesh.SiteMeshContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletContext;

/**
 * @author Joe Walnes
 * @since SiteMesh 3
 */
public class SiteMeshWebAppContext implements SiteMeshContext {

    private static final String IS_USING_STRING_KEY = "com.opensymphony.sitemesh.USINGSTREAM";

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ServletContext servletContext;

    private String contentType;

    public SiteMeshWebAppContext(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public boolean isUsingStream() {
        return request.getAttribute(IS_USING_STRING_KEY) == Boolean.TRUE;
    }

    public void setUsingStream(boolean isUsingStream) {
        request.setAttribute(IS_USING_STRING_KEY, isUsingStream ? Boolean.TRUE : Boolean.FALSE); // JDK 1.3 friendly
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
