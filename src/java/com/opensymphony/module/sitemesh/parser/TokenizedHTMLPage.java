package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.SitemeshWriter;
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

    private SitemeshBufferFragment body;
    private SitemeshBufferFragment head;

    public TokenizedHTMLPage(SitemeshBuffer sitemeshBuffer) {
        super(sitemeshBuffer);
        addProperty("title", "");
    }

    public void setBody(SitemeshBufferFragment body) {
        this.body = body;
    }

    public void setHead(SitemeshBufferFragment head) {
        this.head = head;
    }

    public void writeHead(Writer out) throws IOException {
        if (out instanceof SitemeshWriter) {
            ((SitemeshWriter) out).writeSitemeshBufferFragment(head);
        } else {
            head.writeTo(out);
        }
    }

    public void writeBody(Writer out) throws IOException {
        if (out instanceof SitemeshWriter) {
            ((SitemeshWriter) out).writeSitemeshBufferFragment(body);
        } else {
            body.writeTo(out);
        }
    }

    public String getHead() {
        return head.toString();
    }

    public String getBody() {
        return body.toString();
    }
}
