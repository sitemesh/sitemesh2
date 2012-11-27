package com.opensymphony.module.sitemesh.scalability.outputlength;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.SitemeshWriter;

import java.io.IOException;
import java.io.Writer;

/**
 * A SiteMeshWriter that can observe characters as they are written to out.
 */
public class OutputLengthObservantSitemeshWriter extends Writer implements SitemeshWriter
{
    private final OutputLengthObserver outputLengthObserver;
    private final SitemeshWriter delegate;

    public OutputLengthObservantSitemeshWriter(final OutputLengthObserver outputLengthObserver, final SitemeshWriter delegate)
    {
        super();
        this.outputLengthObserver = outputLengthObserver;
        this.delegate = delegate;
    }

    public Writer getUnderlyingWriter()
    {
        return this;
    }

    public void write(int c) throws IOException
    {
        outputLengthObserver.nChars(1);
        delegate.write(c);
    }

     public void write(char[] chars, int off, int len)  throws IOException
    {
        outputLengthObserver.nChars(len - off);
        delegate.write(chars, off, len);
    }

    public void write(char[] chars) throws IOException
    {
        outputLengthObserver.nChars(chars.length);
        delegate.write(chars);
    }

    public void write(String str, int off, int len)  throws IOException
    {
        outputLengthObserver.nChars(len - off);
        delegate.write(str, off, len);
    }

    public void write(String str) throws IOException
    {
        outputLengthObserver.nChars(str.length());
        delegate.write(str);
    }

    @Override
    public void flush() throws IOException
    {
        delegate.flush();
    }

    @Override
    public void close() throws IOException
    {
        delegate.close();
    }

    public boolean writeSitemeshBufferFragment(SitemeshBufferFragment bufferFragment) throws IOException
    {
        return delegate.writeSitemeshBufferFragment(bufferFragment);
    }

    public SitemeshBuffer getSitemeshBuffer()
    {
        return delegate.getSitemeshBuffer();
    }
}
