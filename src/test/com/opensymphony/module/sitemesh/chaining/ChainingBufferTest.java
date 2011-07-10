package com.opensymphony.module.sitemesh.chaining;

import java.io.CharArrayWriter;
import java.util.Arrays;

import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;

import com.opensymphony.module.sitemesh.SitemeshBufferWriter;
import junit.framework.TestCase;

/**
 */
public class ChainingBufferTest extends TestCase {
    public void testSimpleChain() throws Exception {
        SitemeshBuffer buffer = newSitemeshBuffer("1234", newBufferFragment("ab", 2));
        assertEquals("12ab34", getContent(buffer));
    }

    public void testBefore() throws Exception {
        SitemeshBuffer buffer = newSitemeshBuffer("1234", newBufferFragment("ab", 2));
        assertEquals("1", getContent(buffer, 0, 1));
        assertEquals("12", getContent(buffer, 0, 2));
    }

    public void testAfter() throws Exception {
        SitemeshBuffer buffer = newSitemeshBuffer("1234", newBufferFragment("ab", 2));
        assertEquals("ab34", getContent(buffer, 2, 2));
        assertEquals("4", getContent(buffer, 3, 1));
    }

    public void testFragment() throws Exception {
        SitemeshBuffer buffer = newSitemeshBuffer("1234", newBufferFragment("abcd", 1, 2, 2));
        assertEquals("12bc34", getContent(buffer));
    }

    public void testDeepFragments() throws Exception {
        SitemeshBuffer buffer = newSitemeshBuffer("123456789",
                newBufferFragment("abcdefg", 3,
                        newBufferFragment("hijklm", 1, 1, 4),
                        newBufferFragment("nopqr", 1, 4, 5)),
                newBufferFragment("tuzwx", 0, 2, 8));
        assertEquals("123abcdieopqrfg45678tu9", getContent(buffer));
    }

    public void testWriter() throws Exception {
        SitemeshBuffer buffer = newSitemeshBuffer("123456");
        SitemeshBufferWriter writer = new SitemeshBufferWriter();
        writer.write("abc");
        writer.writeSitemeshBuffer(buffer, 1, 4);
        writer.write("def");
        assertEquals("abcdef", writer.toString());
        assertEquals("abc2345def", getContent(writer.getSitemeshBuffer()));
    }

    private String getContent(SitemeshBuffer buffer) throws Exception {
        CharArrayWriter writer = new CharArrayWriter();
        buffer.writeTo(writer, 0, buffer.getBufferLength());
        return writer.toString();
    }

    private String getContent(SitemeshBuffer buffer, int start, int length) throws Exception {
        CharArrayWriter writer = new CharArrayWriter();
        buffer.writeTo(writer, start, length);
        return writer.toString();
    }

    private SitemeshBuffer newSitemeshBuffer(String content, SitemeshBufferFragment... fragments) {
        return new DefaultSitemeshBuffer(content.toCharArray(), content.length(), Arrays.asList(fragments));
    }

    private SitemeshBufferFragment newBufferFragment(String content, int position) {
        return new SitemeshBufferFragment(newSitemeshBuffer(content), 0, content.length(), position);
    }

    private SitemeshBufferFragment newBufferFragment(String content, int start, int length, int position) {
        return new SitemeshBufferFragment(newSitemeshBuffer(content), start, length, position);
    }

    private SitemeshBufferFragment newBufferFragment(String content, int position, SitemeshBufferFragment... fragments) {
        return new SitemeshBufferFragment(newSitemeshBuffer(content, fragments), 0, content.length(), position);
    }
}
