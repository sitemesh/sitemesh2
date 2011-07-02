package com.opensymphony.module.sitemesh.chaining;

import java.io.CharArrayWriter;
import java.util.Arrays;

import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;

import junit.framework.TestCase;

/**
 */
public class ChainingBufferTest extends TestCase
{
    public void testSimpleChain() throws Exception
    {
        SitemeshBuffer buffer = newSitemeshBuffer("aaaa", newBufferFragment("bb", 2));
        assertEquals("aabbaa", getContent(buffer));
    }

    public void testBefore() throws Exception
    {
        SitemeshBuffer buffer = newSitemeshBuffer("aaaa", newBufferFragment("bb", 2));
        assertEquals("a", getContent(buffer, 0, 1));
        assertEquals("aa", getContent(buffer, 0, 2));
    }

    public void testAfter() throws Exception
    {
        SitemeshBuffer buffer = newSitemeshBuffer("aaaa", newBufferFragment("bb", 2));
        assertEquals("bbaa", getContent(buffer, 2, 2));
        assertEquals("a", getContent(buffer, 3, 1));
    }

    public void 

    private String getContent(SitemeshBuffer buffer) throws Exception
    {
        CharArrayWriter writer = new CharArrayWriter();
        buffer.writeTo(writer, 0, buffer.getBufferLength());
        return writer.toString();
    }

    private String getContent(SitemeshBuffer buffer, int start, int length) throws Exception
    {
        CharArrayWriter writer = new CharArrayWriter();
        buffer.writeTo(writer, start, length);
        return writer.toString();
    }

    private SitemeshBuffer newSitemeshBuffer(String content, SitemeshBufferFragment... fragments)
    {
        return new DefaultSitemeshBuffer(content.toCharArray(), content.length(), Arrays.asList(fragments));
    }

    private SitemeshBufferFragment newBufferFragment(String content, int position)
    {
        return new SitemeshBufferFragment(newSitemeshBuffer(content), 0, content.length(), position);
    }
}
