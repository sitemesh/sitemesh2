package com.opensymphony.module.sitemesh;

import com.opensymphony.module.sitemesh.util.CharArrayWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A char array writer that caches other sitemesh buffers written to it, so that it doesn't have to continually copy
 * them from buffer to buffer.
 */
public class SitemeshBufferWriter extends CharArrayWriter implements SitemeshWriter {

    private final List<SitemeshBufferFragment> fragments = new ArrayList<SitemeshBufferFragment>();

    public SitemeshBufferWriter() {
    }

    public SitemeshBufferWriter(int initialSize) {
        super(initialSize);
    }

    public boolean writeSitemeshBuffer(SitemeshBuffer sitemeshBuffer, int start, int length) throws IOException {
        fragments.add(new SitemeshBufferFragment(sitemeshBuffer, start, length, count));
        return false;
    }

    public SitemeshBuffer getSitemeshBuffer() {
        return new DefaultSitemeshBuffer(buf, count, fragments);
    }
}
