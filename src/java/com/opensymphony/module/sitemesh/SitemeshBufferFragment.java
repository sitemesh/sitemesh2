package com.opensymphony.module.sitemesh;

/**
 * A fragment of a sitemesh buffer
 */
public class SitemeshBufferFragment implements Comparable<SitemeshBufferFragment> {
    private final SitemeshBuffer buffer;
    private final int position;
    private final int start;
    private final int length;

    public SitemeshBufferFragment(SitemeshBuffer buffer, int start, int length, int position) {
        this.buffer = buffer;
        this.start = start;
        this.length = length;
        this.position = position;
    }

    public SitemeshBuffer getBuffer() {
        return buffer;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }

    public int getPosition() {
        return position;
    }

    public int compareTo(SitemeshBufferFragment o) {
        return o.position - position;
    }
}
