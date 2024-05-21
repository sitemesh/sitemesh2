package com.opensymphony.module.sitemesh.filter;

import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.SitemeshBufferWriter;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshWriter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * A sitemesh print writer
 */
public class SitemeshPrintWriter extends PrintWriter implements SitemeshWriter {

    private final SitemeshWriter sitemeshWriter;

    public SitemeshPrintWriter(SitemeshWriter sitemeshWriter) {
        super(sitemeshWriter.getUnderlyingWriter());
        this.sitemeshWriter = sitemeshWriter;
    }

    public Writer getUnderlyingWriter()
    {
        return this;
    }

    public boolean writeSitemeshBufferFragment(SitemeshBufferFragment bufferFragment) throws IOException {
        return sitemeshWriter.writeSitemeshBufferFragment(bufferFragment);
    }

    public SitemeshBuffer getSitemeshBuffer() {
        return sitemeshWriter.getSitemeshBuffer();
    }
}
