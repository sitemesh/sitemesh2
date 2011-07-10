package com.opensymphony.module.sitemesh.filter;

import com.opensymphony.module.sitemesh.SitemeshBufferWriter;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshWriter;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * A sitemesh print writer
 */
public class SitemeshPrintWriter extends PrintWriter implements SitemeshWriter {

    private final SitemeshWriter sitemeshWriter;

    public SitemeshPrintWriter(SitemeshBufferWriter sitemeshWriter) {
        super(sitemeshWriter);
        this.sitemeshWriter = sitemeshWriter;
    }

    public boolean writeSitemeshBuffer(SitemeshBuffer sitemeshBuffer, int start, int length) throws IOException {
        return sitemeshWriter.writeSitemeshBuffer(sitemeshBuffer, start, length);
    }

    public SitemeshBuffer getSitemeshBuffer() {
        return sitemeshWriter.getSitemeshBuffer();
    }
}
