package com.opensymphony.module.sitemesh.filter;

public class BufferedContent {
    private final char[] buffer;
    private final int length;

    public BufferedContent(char[] buffer) {
        this.buffer = buffer;
        this.length = buffer.length;
    }

    public BufferedContent(char[] buffer, int length) {
        this.buffer = buffer;
        this.length = length;
    }

    public char[] getBuffer() {
        return buffer;
    }

    public int getLength() {
        return length;
    }
}
