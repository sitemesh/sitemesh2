package com.opensymphony.module.sitemesh;

/**
 *
 */
public class SitemeshBufferFragmentDeletion
{
    private final int start;
    private final int length;

    public SitemeshBufferFragmentDeletion(int start, int length)
    {
        this.start = start;
        this.length = length;
    }

    public int getStart()
    {
        return start;
    }

    public int getLength()
    {
        return length;
    }
}
