/*
 * Title:        PageWrapper
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.Page;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;

/**
 * Wrapper class that relays all methods to an original Page.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.2 $
 */
public class PageWrapper extends AbstractPage implements Page {
    private Page original = null;

    /** Create wrapper around original Page passed in. */
    public PageWrapper(Page original) {
        this.original = original;
    }

    /** Get original Page. */
    protected Page page() {
        return original;
    }

    public void writePage(Writer out) throws IOException {
        page().writePage(out);
    }

    public void writeBody(Writer out) throws IOException {
        page().writeBody(out);
    }

    public String getTitle() {
        return page().getTitle();
    }

    public int getContentLength() {
        return page().getContentLength();
    }

    public String getProperty(String name) {
        return page().getProperty(name);
    }

    public int getIntProperty(String name) {
        return page().getIntProperty(name);
    }

    public long getLongProperty(String name) {
        return page().getLongProperty(name);
    }

    public boolean getBooleanProperty(String name) {
        return page().getBooleanProperty(name);
    }

    public boolean isPropertySet(String name) {
        return page().isPropertySet(name);
    }

    public String[] getPropertyKeys() {
        return page().getPropertyKeys();
    }

    public HttpServletRequest getRequest() {
        return page().getRequest();
    }

    public void setRequest(HttpServletRequest request) {
        page().setRequest(request);
    }
}