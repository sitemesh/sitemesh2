package com.opensymphony.module.sitemesh.parser;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.SitemeshWriter;

public class PartialPageParserPage extends AbstractPage {

    private final SitemeshBufferFragment body;

    public PartialPageParserPage(SitemeshBuffer sitemeshBuffer, SitemeshBufferFragment body) {
        super(sitemeshBuffer);
        this.body = body;
    }

    @Override
    public void writeBody(Writer out) throws IOException {
        if (out instanceof SitemeshWriter) {
            ((SitemeshWriter) out).writeSitemeshBufferFragment(body);
        } else {
            body.writeTo(out);
        }
    }
}
