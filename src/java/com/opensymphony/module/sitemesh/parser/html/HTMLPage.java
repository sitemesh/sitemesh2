package com.opensymphony.module.sitemesh.parser.html;

import com.opensymphony.module.sitemesh.parser.AbstractHTMLPage;

import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;

class HTMLPage extends AbstractHTMLPage {

    private Writer head = new StringWriter();
    private Writer body = new StringWriter();

    public HTMLPage(char[] original) {
        this.pageData = original;
    }

    public void appendToHead(String s) {
        try {
            head.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendToBody(String s) {
        try {
            body.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeHead(Writer out) throws IOException {
        out.write(head.toString());
    }

    public void writeBody(Writer out) throws IOException {
        out.write(body.toString());
    }

    public String getHead() {
        return null;
    }

    public boolean isFrameSet() {
        return false;
    }

}
