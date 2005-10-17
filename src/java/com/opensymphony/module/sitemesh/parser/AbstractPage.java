/*
 * Title:        AbstractPage
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Abstract implementation of {@link com.opensymphony.module.sitemesh.Page} .
 *
 * <p>Contains base methods for storing and accessing page properties.
 * Also stores {@link #pageData} as byte[] and implements write???()
 * methods.</p>
 *
 * <p>Concrete implementations need only set the {@link #pageData} and
 * call {@link #addProperty(java.lang.String,java.lang.String)} to
 * add all the required information.</p>
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.6 $
 *
 * @see com.opensymphony.module.sitemesh.Page
 */
public abstract class AbstractPage implements Page {
    /**
     * Map of all properties.
     * Key is String. Value is java.util.List of multiple String values.
     */
    private final Map properties = new HashMap();

    /** Date of page contents. */
    protected char[] pageData = new char[0];

    /** RequestURI of original Page. */
    private HttpServletRequest request;

    public void writePage(Writer out) throws IOException {
        out.write(pageData);
    }

    public String getPage() {
        try {
            StringWriter writer = new StringWriter();
            writePage(writer);
            return writer.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Could not get page " + e.getMessage());
        }
    }

    /**
     * Write data of html <code>&lt;body&gt;</code> tag.
     *
     * <p>Must be implemented. Data written should not actually contain the
     * body tags, but all the data in between.
     */
    public abstract void writeBody(Writer out) throws IOException;

    public String getBody() {
        try {
            StringWriter writer = new StringWriter();
            writeBody(writer);
            return writer.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Could not get body " + e.getMessage());
        }
    }

    /** Return title of from "title" property. Never returns null. */
    public String getTitle() {
        return noNull(getProperty("title"));
    }

    public int getContentLength() {
        try
        {
            //todo - this needs to be fixed properly (SIM-196)
            return new String(pageData).getBytes("UTF-8").length; // we cannot just measure pageData.length, due to i18n issues (SIM-157)
        }
        catch (UnsupportedEncodingException e)
        {
            return new String(pageData).getBytes().length;
        }
    }

    public String getProperty(String name) {
        if (!isPropertySet(name)) return null;
        return (String)properties.get(name);
    }

    public int getIntProperty(String name) {
        try {
            return Integer.parseInt(noNull(getProperty(name)));
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public long getLongProperty(String name) {
        try {
            return Long.parseLong(noNull(getProperty(name)));
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean getBooleanProperty(String name) {
        String property = getProperty(name);
        if (property == null || property.trim().length() == 0) return false;
        switch (property.charAt(0)) {
            case '1':
            case 't':
            case 'T':
            case 'y':
            case 'Y':
                return true;
            default:
                return false;
        }
    }

    public boolean isPropertySet(String name) {
        return properties.containsKey(name);
    }

    public String[] getPropertyKeys() {
        synchronized(properties) {
            Set keys = properties.keySet();
            return (String[])keys.toArray(new String[keys.size()]);
        }
    }

    public Map getProperties() {
        return properties;
    }

    /** @see com.opensymphony.module.sitemesh.Page#getRequest() */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * Create snapshot of Request.
     *
     * @see com.opensymphony.module.sitemesh.Page#getRequest()
     */
    public void setRequest(HttpServletRequest request) {
        this.request = new PageRequest(request);
    }

    /**
     * Add a property to the properties list.
     *
     * @param name Name of property
     * @param value Value of property
     */
    public void addProperty(String name, String value) {
        properties.put(name, value);
    }

    /** Return String as is, or "" if null. (Prevents NullPointerExceptions) */
    protected static String noNull(String in) {
        return in == null ? "" : in;
    }
}

class PageRequest extends HttpServletRequestWrapper {

    private String requestURI, method, pathInfo, pathTranslated, queryString, servletPath;

    public PageRequest(HttpServletRequest request) {
        super(request);
        requestURI = request.getRequestURI();
        method = request.getMethod();
        pathInfo = request.getPathInfo();
        pathTranslated = request.getPathTranslated();
        queryString = request.getQueryString();
        servletPath = request.getServletPath();
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getMethod() {
        return method;
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public String getPathTranslated() {
        return pathTranslated;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getServletPath() {
        return servletPath;
    }
}
