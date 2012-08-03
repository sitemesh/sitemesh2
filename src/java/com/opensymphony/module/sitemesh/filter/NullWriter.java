package com.opensymphony.module.sitemesh.filter;

import java.io.IOException;
import java.io.Writer;

/**
 * Just to keep super constructor for PrintWriter happy - it's never actually used.
 */
public class NullWriter extends Writer
{
    public void write(char cbuf[], int off, int len) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public void flush() throws IOException {
        throw new UnsupportedOperationException();
    }

    public void close() throws IOException {
        throw new UnsupportedOperationException();
    }

}
