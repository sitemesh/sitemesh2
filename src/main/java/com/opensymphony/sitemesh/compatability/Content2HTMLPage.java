package com.opensymphony.sitemesh.compatability;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.sitemesh.Content;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.HashMap;

/**
 * Adapts a SiteMesh 3 {@link Content} to a SiteMesh 2 {@link HTMLPage}.
 *
 * @author Joe Walnes
 * @since SiteMesh 3
 */
public class Content2HTMLPage implements HTMLPage {

    private final Content content;
    private HttpServletRequest request;

    public Content2HTMLPage(Content content, HttpServletRequest request) {
        this.content = content;
        this.request = request;
    }

    public void writePage(Writer out) throws IOException {
        content.writeOriginal(out);
    }

    public String getPage() {
        try {
            StringWriter writer = new StringWriter();
            writePage(writer);
            return writer.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Could not get page " + e.getMessage(), e);
        }
    }

    public void writeBody(Writer out) throws IOException {
        content.writeBody(out);
    }

    public String getBody() {
        try {
            StringWriter writer = new StringWriter();
            writeBody(writer);
            return writer.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Could not get body " + e.getMessage(), e);
        }
    }

    public void writeHead(Writer out) throws IOException {
        content.writeHead(out);
    }

    public String getHead() {
        try {
            StringWriter writer = new StringWriter();
            writeHead(writer);
            return writer.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Could not get head " + e.getMessage(), e);
        }
    }

    public String getTitle() {
        return content.getTitle();
    }

    public String getProperty(String name) {
        return content.getProperty(name);
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
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String noNull(String property) {
        return property == null ? "" : property;
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
        return getProperty(name) != null;
    }

    public String[] getPropertyKeys() {
        return content.getPropertyKeys();
    }

    public Map getProperties() {
        Map result = new HashMap();
        String[] keys = content.getPropertyKeys();
        for (int i = 0; i < keys.length; i++) {
            result.put(keys[i], content.getProperty(keys[i]));
        }
        return result;
    }

    public boolean isFrameSet() {
        return isPropertySet("frameset") && getProperty("frameset").equalsIgnoreCase("true");
    }

    public void setFrameSet(boolean frameset) {
        addProperty("frameset", frameset ? "true" : "false");
    }

    /**
     * @see com.opensymphony.module.sitemesh.Page#getRequest()
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * Create snapshot of Request.
     *
     * @see com.opensymphony.module.sitemesh.Page#getRequest()
     */
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void addProperty(String name, String value) {
        content.addProperty(name, value);
    }

}
