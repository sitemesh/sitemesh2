package com.opensymphony.module.sitemesh.scalability.secondarystorage;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 */
public class InMemorySecondaryStorage implements SecondaryStorage
{
    private StringWriter sw = new StringWriter();
    private final long memoryLimitBeforeUse;

    public InMemorySecondaryStorage(final long memoryLimitBeforeUse)
    {
        this.memoryLimitBeforeUse = memoryLimitBeforeUse;
    }

    public long getMemoryLimitBeforeUse()
    {
        return memoryLimitBeforeUse;
    }

    public void write(int c)
    {
        sw.write(c);
    }

    public void write(char[] chars, int off, int len)
    {
        sw.write(chars, off, len);
    }

    public void write(String str, int off, int len)
    {
        sw.write(str, off, len);
    }

    public void write(String str)
    {
        sw.write(str);
    }

    public void writeTo(Writer out) throws IOException
    {
       out.write(sw.toString());
    }

    public void cleanUp()
    {
    }
}
