package com.opensymphony.module.sitemesh.filter;

import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
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

    @Override
    public void write(int c) {
        super.write(c);
        super.flush();
    }

    @Override
    public void write(char[] buf, int off, int len) {
        super.write(buf, off, len);
        super.flush();
    }

    @Override
    public void write(String s, int off, int len) {
        super.write(s, off, len);
        super.flush();
    }

    public boolean writeSitemeshBufferFragment(SitemeshBufferFragment bufferFragment) throws IOException {
        return sitemeshWriter.writeSitemeshBufferFragment(bufferFragment);
    }

    public SitemeshBuffer getSitemeshBuffer() {
        return sitemeshWriter.getSitemeshBuffer();
    }
}
