package com.opensymphony.module.sitemesh.scalability.secondarystorage;

import java.io.IOException;
import java.io.Writer;

/**
*/
class TeeWriter extends Writer
{

    private final Writer out;
    private final Writer out2;

    public TeeWriter(Writer out, Writer out2)
    {
        super(out);
        this.out = out;
        this.out2 = out2;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException
    {
        out.write(cbuf,off,len);
        out2.write(cbuf,off,len);
    }

    @Override
    public void flush() throws IOException
    {
        out.flush();
        out2.flush();
    }

    @Override
    public void close() throws IOException
    {
        out.close();
        out2.close();
    }
}
