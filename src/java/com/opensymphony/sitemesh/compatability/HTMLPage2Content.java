package com.opensymphony.sitemesh.compatability;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.sitemesh.Content;

import java.io.IOException;
import java.io.Writer;

/**
 * Adapts a SiteMesh 2 {@link HTMLPage} to a SiteMesh 3 {@link Content}.
 *
 * @author Joe Walnes
 * @since SiteMesh 3
 */
public class HTMLPage2Content implements Content {
    private final HTMLPage page;

    public HTMLPage2Content(HTMLPage page) {
        this.page = page;
    }

    public void writeOriginal(Writer out) throws IOException {
        page.writePage(out);
    }

    public int originalLength() {
        return page.getContentLength();
    }

    public void writeBody(Writer out) throws IOException {
        page.writeBody(out);
    }

    public void writeHead(Writer out) throws IOException {
        page.writeHead(out);
    }

    public String getTitle() {
        return page.getTitle();
    }

    public String getProperty(String name) {
        return page.getProperty(name);
    }

    public String[] getPropertyKeys() {
        return page.getPropertyKeys();
    }

    public void addProperty(String name, String value) {
        page.addProperty(name, value);
    }
}
