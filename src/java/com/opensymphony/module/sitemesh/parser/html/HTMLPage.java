package com.opensymphony.module.sitemesh.parser.html;

import com.opensymphony.module.sitemesh.parser.AbstractHTMLPage;
import com.opensymphony.module.sitemesh.util.CharArray;

import java.io.IOException;
import java.io.Writer;

class HTMLPage extends AbstractHTMLPage {

    private final CharArray head = new CharArray(512);
    private final CharArray body = new CharArray(4096);

    public HTMLPage(char[] original) {
        this.pageData = original;
    }

    public void clearBody() {
        body.clear();
    }

    public void appendToHead(String s) {
        head.append(s);
    }

    public void appendToBody(String s) {
        body.append(s);
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
