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
    private final List<SitemeshBufferFragment> bufferFragments;

    public DefaultSitemeshBuffer(char[] buffer) {
        this(buffer, buffer.length);
    }

    public DefaultSitemeshBuffer(char[] buffer, int length) {
        this(buffer, length, Collections.<SitemeshBufferFragment>emptyList());
    }

    public DefaultSitemeshBuffer(char[] buffer, int length, List<SitemeshBufferFragment> bufferFragments) {
        this.buffer = buffer;
        this.length = length;
        this.bufferFragments = new ArrayList<SitemeshBufferFragment>(bufferFragments);
        Collections.sort(bufferFragments);
    }

    public void writeTo(Writer writer, int start, int length) throws IOException {
        int pos = start;
        for (SitemeshBufferFragment fragment : bufferFragments) {
            if (fragment.getPosition() < pos) {
                continue;
            }
            if (fragment.getPosition() >= start + length) {
                break;
            }
            // Write the buffer up to the fragment
            writer.write(buffer, pos, fragment.getPosition() - pos);
            // Write the fragment
            fragment.getBuffer().writeTo(writer, fragment.getStart(), fragment.getLength());
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

        for (SitemeshBufferFragment fragment : bufferFragments) {
            if (fragment.getPosition() < start) {
                continue;
            }
            if (fragment.getPosition() > start + length) {
                break;
            }
            total += fragment.getBuffer().getTotalLength(fragment.getStart(), fragment.getLength());
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
}
