package com.opensymphony.module.sitemesh;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * The default implementation of sitemesh buffer
 */
public class DefaultSitemeshBuffer implements SitemeshBuffer {

    private final char[] buffer;
    private final int length;
    private final TreeMap<Integer, SitemeshBufferFragment> bufferFragments;

    public DefaultSitemeshBuffer(char[] buffer) {
        this(buffer, buffer.length);
    }

    public DefaultSitemeshBuffer(char[] buffer, int length) {
        this(buffer, length, new TreeMap<Integer, SitemeshBufferFragment>());
    }

    public DefaultSitemeshBuffer(char[] buffer, int length, TreeMap<Integer, SitemeshBufferFragment> bufferFragments) {
        this.buffer = buffer;
        this.length = length;
        this.bufferFragments = bufferFragments;
    }

    public void writeTo(Writer writer, int start, int length) throws IOException {
        int pos = start;
        for (Map.Entry<Integer, SitemeshBufferFragment> entry : bufferFragments.entrySet()) {
            int fragmentPosition = entry.getKey();
            if (fragmentPosition < pos) {
                continue;
            }
            if (fragmentPosition > start + length) {
                break;
            }
            // Write the buffer up to the fragment
            writer.write(buffer, pos, fragmentPosition - pos);
            // Write the fragment
            entry.getValue().writeTo(writer);
            // increment pos
            pos = fragmentPosition;
        }
        // Write out the remaining buffer
        if (pos < start + length) {
            writer.write(buffer, pos, (start + length) - pos);
        }
    }

    public int getTotalLength() {
        return getTotalLength(0, length);
    }

    public int getTotalLength(int start, int length) {
        int total = length;

        for (Map.Entry<Integer, SitemeshBufferFragment> entry : bufferFragments.entrySet()) {
            int fragmentPosition = entry.getKey();
            if (fragmentPosition < start) {
                continue;
            }
            if (fragmentPosition > start + length) {
                break;
            }
            total += entry.getValue().getTotalLength();
        }
        return total;
    }

    public int getBufferLength() {
        return length;
    }

    public char[] getCharArray() {
        return buffer;
    }

    public boolean hasFragments() {
        return !bufferFragments.isEmpty();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SitemeshBuffer sitemeshBuffer) {
        return new Builder((DefaultSitemeshBuffer) sitemeshBuffer);
    }

    public static class Builder {
        private char[] buffer;
        private int length;
        private final TreeMap<Integer, SitemeshBufferFragment> fragments;

        private Builder() {
            this.fragments = new TreeMap<Integer, SitemeshBufferFragment>();
        }

        private Builder(DefaultSitemeshBuffer buffer) {
            this.buffer = buffer.buffer;
            this.length = buffer.length;
            this.fragments = new TreeMap<Integer, SitemeshBufferFragment>(buffer.bufferFragments);
        }

        public Builder setBuffer(char[] buffer) {
            this.buffer = buffer;
            return this;
        }

        public Builder setLength(int length) {
            this.length = length;
            return this;
        }

        public Builder insert(int position, SitemeshBufferFragment fragment) {
            this.fragments.put(position, fragment);
            return this;
        }

        public SitemeshBuffer build() {
            return new DefaultSitemeshBuffer(buffer, length, fragments);
        }
    }
}
