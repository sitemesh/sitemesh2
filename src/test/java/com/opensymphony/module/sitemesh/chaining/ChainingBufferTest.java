package com.opensymphony.module.sitemesh.chaining;

import java.io.CharArrayWriter;
import java.util.Arrays;
import java.util.TreeMap;

import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;

import com.opensymphony.module.sitemesh.SitemeshBufferWriter;
import junit.framework.TestCase;

/**
 */
public class ChainingBufferTest extends TestCase {
    public void testSimpleChain() throws Exception {
        SitemeshBuffer buffer = newSitemeshBuffer("1234", 2, newBufferFragment("ab"));
        assertEquals("12ab34", getContent(buffer));
        assertCorrectLength(buffer);
    }

    public void testBefore() throws Exception {
        SitemeshBuffer buffer = newSitemeshBuffer("1234", 2, newBufferFragment("ab"));
        assertEquals("1", getContent(buffer, 0, 1));
        assertEquals("12ab", getContent(buffer, 0, 2));
    }

    public void testAfter() throws Exception {
        SitemeshBuffer buffer = newSitemeshBuffer("1234", 2, newBufferFragment("ab"));
        assertEquals("ab34", getContent(buffer, 2, 2));
        assertEquals("4", getContent(buffer, 3, 1));
    }

    public void testFragment() throws Exception {
        SitemeshBuffer buffer = newSitemeshBuffer("1234", 2, newBufferFragment("abcd", 1, 2));
        assertEquals("12bc34", getContent(buffer));
        assertCorrectLength(buffer);
    }

    public void testDeepFragments() throws Exception {
        SitemeshBuffer buffer = newSitemeshBuffer("123456789",
                3, newBufferFragment("abcdefg",
                        4, newBufferFragment("hijklm", 1, 1),
                        5, newBufferFragment("nopqr", 1, 4)),
                8, newBufferFragment("tuzwx", 0, 2));
        assertEquals("123abcdieopqrfg45678tu9", getContent(buffer));
        assertCorrectLength(buffer);
    }

    public void testWriter() throws Exception {
        SitemeshBuffer buffer = newSitemeshBuffer("123456");
        SitemeshBufferWriter writer = new SitemeshBufferWriter();
        writer.write("abc");
        writer.writeSitemeshBufferFragment(new SitemeshBufferFragment(buffer, 1, 4));
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

    private void assertCorrectLength(SitemeshBuffer buffer) throws Exception {
        assertEquals(getContent(buffer).length(), buffer.getTotalLength());
    }

    private SitemeshBuffer newSitemeshBuffer(String content) {
        return new DefaultSitemeshBuffer(content.toCharArray(), content.length());
    }

    private SitemeshBuffer newSitemeshBuffer(String content, int pos1, SitemeshBufferFragment frag1) {
        TreeMap<Integer, SitemeshBufferFragment> fragments = new TreeMap<Integer, SitemeshBufferFragment>();
        fragments.put(pos1, frag1);
        return new DefaultSitemeshBuffer(content.toCharArray(), content.length(), fragments);
    }

    private SitemeshBuffer newSitemeshBuffer(String content, int pos1, SitemeshBufferFragment frag1, int pos2, SitemeshBufferFragment frag2) {
        TreeMap<Integer, SitemeshBufferFragment> fragments = new TreeMap<Integer, SitemeshBufferFragment>();
        fragments.put(pos1, frag1);
        fragments.put(pos2, frag2);
        return new DefaultSitemeshBuffer(content.toCharArray(), content.length(), fragments);
    }

    private SitemeshBufferFragment newBufferFragment(String content) {
        return new SitemeshBufferFragment(newSitemeshBuffer(content), 0, content.length());
    }

    private SitemeshBufferFragment newBufferFragment(String content, int start, int length) {
        return new SitemeshBufferFragment(newSitemeshBuffer(content), start, length);
    }

    private SitemeshBufferFragment newBufferFragment(String content, int pos1, SitemeshBufferFragment frag1) {
        return new SitemeshBufferFragment(newSitemeshBuffer(content, pos1, frag1), 0, content.length());
    }

    private SitemeshBufferFragment newBufferFragment(String content, int pos1, SitemeshBufferFragment frag1, int pos2, SitemeshBufferFragment frag2) {
        return new SitemeshBufferFragment(newSitemeshBuffer(content, pos1, frag1, pos2, frag2), 0, content.length());
    }
}
