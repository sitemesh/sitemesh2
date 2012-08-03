package com.opensymphony.module.sitemesh.scalability.secondarystorage;

import java.io.Reader;

/**
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

    public Reader readBack()
    {
        throw new IllegalStateException("We do not expect this method to be called since we return -1 in getMemoryLimitBeforeUse()");
    }

    public void close()
    {
    }
}
