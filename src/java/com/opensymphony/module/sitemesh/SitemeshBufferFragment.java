package com.opensymphony.module.sitemesh;

import com.opensymphony.module.sitemesh.html.util.StringSitemeshBuffer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

/**
 * A fragment of a sitemesh buffer.  This includes a start and a length, and may contain a list of deleted sections of
 * the buffer.
 */
public class SitemeshBufferFragment {
    private final SitemeshBuffer buffer;
    private final int start;
    private final int length;
    private final TreeMap<Integer, Integer> deletions;

    public SitemeshBufferFragment(SitemeshBuffer buffer, int start, int length) {
        this(buffer, start, length, new TreeMap<Integer, Integer>());
    }

    /**
     * Create a sitemesh buffer fragment
     *
     * @param buffer    The buffer that this is a fragment of
     * @param start     The start of the fragment
     * @param length    The length of the fragment
     * @param deletions Deleted parts of the fragment, as a map of positions to the length to be deleted.
     */
    public SitemeshBufferFragment(SitemeshBuffer buffer, int start, int length, TreeMap<Integer, Integer> deletions) {
        this.buffer = buffer;
        this.start = start;
        this.length = length;
        this.deletions = deletions;
    }

    /**
     * Write the fragment to the given writer
     *
     * @param writer The writer to write the fragment to
     * @throws IOException If an error occured
     */
    public void writeTo(Writer writer) throws IOException {
        int pos = start;
        for (Map.Entry<Integer, Integer> delete : deletions.entrySet()) {
            int deletePos = delete.getKey();
            if (deletePos >= pos) {
                buffer.writeTo(writer, pos, deletePos - pos);
            }
            pos = Math.max(deletePos + delete.getValue(), start);
        }
        int remain = start + length - pos;
        if (remain >= 0) {
            buffer.writeTo(writer, pos, remain);
        }
    }

    /**
     * Get the total length of the fragment, taking deletions and chained buffers of the buffer
     *
     * @return The total length of the fragment
     */
    public int getTotalLength() {
        int total = 0;
        int pos = start;
        for (Map.Entry<Integer, Integer> delete : deletions.entrySet()) {
            int deletePos = delete.getKey();
            if (deletePos > pos) {
                total += buffer.getTotalLength(pos, deletePos - pos);
            }
            pos = deletePos + delete.getValue();
        }
        int remain = start + length - pos;
        if (remain > 0) {
            total += buffer.getTotalLength(pos, remain);
        }
        return total;
    }

    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        try {
            writeTo(writer);
        } catch (IOException e) {
            throw new RuntimeException("Exception writing to buffer", e);
        }
        return writer.toString();
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SitemeshBufferFragment fragment) {
        return new Builder(fragment);
    }

    /**
     * A builder for fragments.
     */
    public static class Builder {
        private DefaultSitemeshBuffer.Builder buffer;
        private int start;
        private int length;
        private final TreeMap<Integer, Integer> deletions;

        private Integer startDelete;

        private Builder() {
            this.deletions = new TreeMap<Integer, Integer>();
        }

        private Builder(SitemeshBufferFragment fragment) {
            this.buffer = DefaultSitemeshBuffer.builder(fragment.buffer);
            this.start = fragment.start;
            this.length = fragment.length;
            this.deletions = new TreeMap<Integer, Integer>(fragment.deletions);
        }

        public Builder setStart(int start) {
            this.start = start;
            return this;
        }

        public Builder setLength(int length) {
            this.length = length;
            return this;
        }

        /**
         * Delete length characters from pos in this buffer fragment
         *
         * @param pos    The position to delete from
         * @param length The number of characters to delete
         * @return The builder
         */
        public Builder delete(int pos, int length) {
            this.deletions.put(pos, length);
            return this;
        }

        /**
         * Mark the start of the fragment
         *
         * @param pos The start of the fragment
         * @return The builder
         */
        public Builder markStart(int pos) {
            this.start = pos;
            this.length = 0;
            return this;
        }

        /**
         * End the fragment
         *
         * @param pos The position of the end of the fragment
         * @return The builder
         */
        public Builder end(int pos) {
            this.length = pos - this.start;
            return this;
        }

        /**
         * Mark the start of a deletion.
         *
         * @param pos The position to start deleting from
         * @return The builder
         * @throws IllegalStateException If markStartDelete() has already been called and endDelete() hasn't been called
         */
        public Builder markStartDelete(int pos) {
            if (startDelete != null) {
                throw new IllegalStateException("Can't nested delete...");
            }
            startDelete = pos;
            return this;
        }

        /**
         * End the current deletion
         *
         * @param pos The position to delete to
         * @return The builder
         * @throws IllegalStateException If markStartDelete() hasn't been called
         */
        public Builder endDelete(int pos) {
            if (startDelete == null) {
                throw new IllegalStateException("Ending delete with no start delete...");
            }
            delete(startDelete, pos - startDelete);
            startDelete = null;
            return this;
        }

        /**
         * Insert the given fragment to the given position
         *
         * @param position The position to insert the fragment to
         * @param fragment The fragment to insert
         * @return The builder
         */
        public Builder insert(int position, SitemeshBufferFragment fragment) {
            buffer.insert(position, fragment);
            return this;
        }

        /**
         * Insert the given string fragment to the given position
         *
         * @param position The position to insert at
         * @param fragment The fragment to insert
         * @return The builder
         */
        public Builder insert(int position, String fragment) {
            buffer.insert(position, StringSitemeshBuffer.createBufferFragment(fragment));
            return this;
        }

        /**
         * Set the buffer.  This resets both start and length to be that of the buffer.
         *
         * @param sitemeshBuffer The buffer to set.
         * @return The builder
         */
        public Builder setBuffer(SitemeshBuffer sitemeshBuffer) {
            this.buffer = DefaultSitemeshBuffer.builder(sitemeshBuffer);
            this.start = 0;
            this.length = sitemeshBuffer.getBufferLength();
            return this;
        }

        /**
         * Build the fragment
         *
         * @return The built fragment
         */
        public SitemeshBufferFragment build() {
            return new SitemeshBufferFragment(buffer.build(), start, length, deletions);
        }
    }
}
