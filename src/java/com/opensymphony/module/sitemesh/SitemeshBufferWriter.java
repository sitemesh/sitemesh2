package com.opensymphony.module.sitemesh;

import com.opensymphony.module.sitemesh.outputlength.NoopOutputLengthObserver;
import com.opensymphony.module.sitemesh.outputlength.OutputLengthObserver;
import com.opensymphony.module.sitemesh.util.CharArrayWriter;

import java.io.IOException;
import java.util.TreeMap;

/**
 * A char array writer that caches other sitemesh buffers written to it, so that it doesn't have to continually copy
 * them from buffer to buffer.
 */
public class SitemeshBufferWriter extends CharArrayWriter implements SitemeshWriter {

    private final TreeMap<Integer, SitemeshBufferFragment> fragments = new TreeMap<Integer, SitemeshBufferFragment>();
    private final OutputLengthObserver outputLengthObserver;

     public SitemeshBufferWriter() {
        outputLengthObserver = new NoopOutputLengthObserver();
    }

    public SitemeshBufferWriter(int initialSize) {
        super(initialSize);
        outputLengthObserver = new NoopOutputLengthObserver();
    }

    public SitemeshBufferWriter(int initialSize, OutputLengthObserver outputLengthObserver) {
        super(initialSize);
        this.outputLengthObserver = outputLengthObserver;
    }

    @Override
    public void write(int c) {
        outputLengthObserver.nChars(1);
        super.write(c);
    }

    @Override
    public void write(char[] c, int off, int len) {
        outputLengthObserver.nChars(len);
        super.write(c, off, len);
    }

    @Override
    public void write(String str, int off, int len) {
        outputLengthObserver.nChars(len);
        super.write(str, off, len);
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        outputLengthObserver.nChars(cbuf.length);
        super.write(cbuf);
    }

    public boolean writeSitemeshBufferFragment(SitemeshBufferFragment bufferFragment) throws IOException {
        fragments.put(count, bufferFragment);
        return false;
    }

    public SitemeshBuffer getSitemeshBuffer() {
        return new DefaultSitemeshBuffer(buf, count, fragments);
    }
}
