package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.html.util.CharArray;
import com.opensymphony.module.sitemesh.html.rules.PageBuilder;

import java.io.IOException;
import java.io.Writer;

/**
 * HTMLPage implementation that builds itself based on the incoming 'tag' and 'text' tokens fed to it from the
 * HTMLTagTokenizer.
 *
 * @see com.opensymphony.module.sitemesh.parser.HTMLPageParser
 * @see com.opensymphony.module.sitemesh.html.tokenizer.TagTokenizer
 *
 * @author Joe Walnes
 */
public class TokenizedHTMLPage extends AbstractHTMLPage implements PageBuilder {

    private CharArray body;
    private CharArray head;

    public TokenizedHTMLPage(char[] original, CharArray body, CharArray head) {
        this.pageData = original;
        this.body = body;
        this.head = head;
        addProperty("title", "");
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

    public String getBody() {
        return body.toString();
    }

    public String getPage() {
        return new String(pageData);
    }

}
