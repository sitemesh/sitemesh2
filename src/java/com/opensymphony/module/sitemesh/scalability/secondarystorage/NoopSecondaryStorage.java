package com.opensymphony.module.sitemesh.scalability.secondarystorage;

import java.io.Writer;

/**
 * A secondary storage that does nothing.
  */
public class NoopSecondaryStorage implements SecondaryStorage
{
    public long getMemoryLimitBeforeUse()
    {
        return -1;
    }

     public void write(int c)
    {
    }

    public void write(char[] chars, int off, int len)
    {
    }

    public void write(String str, int off, int len)
    {
    }

    public void write(String str)
    {
    }

    public void writeTo(Writer out)
    {

    }

    public void cleanUp()
    {
    }
}
