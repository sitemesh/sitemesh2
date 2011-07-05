package com.opensymphony.module.sitemesh;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The default implementation of sitemesh buffer
 */
public class DefaultSitemeshBuffer implements SitemeshBuffer {

    private final char[] buffer;
    private final int length;
    private final List<SitemeshBufferFragment> chainedBuffers;

    public DefaultSitemeshBuffer(char[] buffer) {
        this(buffer, buffer.length);
    }

    public DefaultSitemeshBuffer(char[] buffer, int length) {
        this(buffer, length, Collections.<SitemeshBufferFragment>emptyList());
    }

    public DefaultSitemeshBuffer(char[] buffer, int length, List<SitemeshBufferFragment> chainedBuffers) {
        this.buffer = buffer;
        this.length = length;
        this.chainedBuffers = new ArrayList<SitemeshBufferFragment>(chainedBuffers);
        Collections.sort(chainedBuffers);
    }

    public void writeTo(Writer writer, int start, int length) throws IOException {
        int pos = start;
        for (SitemeshBufferFragment fragment : chainedBuffers) {
            if (fragment.getPosition() < pos) {
                continue;
            }
            if (fragment.getPosition() >= start + length) {
                break;
            }
            // Write the buffer up to the fragment
            writer.write(buffer, pos, fragment.getPosition() - pos);
            // Write the fragment
            fragment.writeTo(writer);
            // increment pos
            pos = fragment.getPosition();
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

        for (SitemeshBufferFragment fragment : chainedBuffers) {
            if (fragment.getPosition() < start) {
                continue;
            }
            if (fragment.getPosition() > start + length) {
                break;
            }
            total += fragment.getTotalLength();
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
        return !chainedBuffers.isEmpty();
    }
}
