package com.opensymphony.module.sitemesh.parser.html;

import com.opensymphony.module.sitemesh.parser.AbstractHTMLPage;

import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;

class HTMLPage extends AbstractHTMLPage {

    private StringWriter head = new StringWriter();
    private StringWriter body = new StringWriter();

    public HTMLPage(char[] original) {
        this.pageData = original;
    }

    public void clearBody() {
        body.getBuffer().setLength(0);
    }

    public void appendToHead(String s) {
        head.write(s);
    }

    public void appendToBody(String s) {
        body.write(s);
    }

    public void writeHead(Writer out) throws IOException {
        out.write(head.toString());
    }

    public void writeBody(Writer out) throws IOException {
        out.write(body.toString());
    }

    public String getHead() {
        return head.toString();
    }

    public boolean isFrameSet() {
        return false;
    }

    public String getPage() {
        return body.toString();
    }

    public String getBody() {
        return new String(pageData);
    }


}
