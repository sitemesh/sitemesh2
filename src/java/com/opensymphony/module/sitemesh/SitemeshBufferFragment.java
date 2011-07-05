package com.opensymphony.module.sitemesh;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

/**
 * A fragment of a sitemesh buffer
 */
public class SitemeshBufferFragment implements Comparable<SitemeshBufferFragment> {
    private final SitemeshBuffer buffer;
    private final int position;
    private final int start;
    private final int length;
    private final List<SitemeshBufferFragmentDeletion> deletions;

    public SitemeshBufferFragment(SitemeshBuffer buffer, int start, int length, int position) {
        this(buffer, start, length, position, Collections.<SitemeshBufferFragmentDeletion>emptyList());
    }

    public SitemeshBufferFragment(SitemeshBuffer buffer, int start, int length, int position, List<SitemeshBufferFragmentDeletion> deletions) {
        this.buffer = buffer;
        this.start = start;
        this.length = length;
        this.position = position;
        this.deletions = deletions;
    }

    public void writeTo(Writer writer) throws IOException
    {
        int pos = start;
        for (SitemeshBufferFragmentDeletion deletion : deletions) {
            if (deletion.getStart() > pos) {
                buffer.writeTo(writer, pos, deletion.getStart() - pos);
            }
            pos = deletion.getStart() + deletion.getLength();
        }
        int remain = start + length - pos;
        if (remain > 0)
        {
            buffer.writeTo(writer, pos, remain);
        }
    }

    public int getTotalLength()
    {
        int total = 0;
        int pos = start;
        for (SitemeshBufferFragmentDeletion deletion : deletions) {
            if (deletion.getStart() > pos) {
                total += buffer.getTotalLength(pos, deletion.getStart() - pos);
            }
            pos = deletion.getStart() + deletion.getLength();
        }
        int remain = start + length - pos;
        if (remain > 0)
        {
            total += remain;
        }
        return total;
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
