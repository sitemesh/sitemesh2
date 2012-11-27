package com.opensymphony.module.sitemesh;

import com.opensymphony.module.sitemesh.util.CharArrayWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.TreeMap;

/**
 * A char array writer that caches other sitemesh buffers written to it, so that it doesn't have to continually copy
 * them from buffer to buffer.
 */
public class SitemeshBufferWriter extends CharArrayWriter implements SitemeshWriter {

    private final TreeMap<Integer, SitemeshBufferFragment> fragments = new TreeMap<Integer, SitemeshBufferFragment>();

     public SitemeshBufferWriter() {
    }

    public SitemeshBufferWriter(int initialSize) {
        super(initialSize);
    }

    public Writer getUnderlyingWriter() {
        return this;
    }

    public boolean writeSitemeshBufferFragment(SitemeshBufferFragment bufferFragment) throws IOException {
        fragments.put(count, bufferFragment);
        return false;
    }

    public SitemeshBuffer getSitemeshBuffer() {
        return new DefaultSitemeshBuffer(buf, count, fragments);
    }
}
