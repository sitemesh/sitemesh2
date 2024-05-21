/*
 * Title:        FastByteArrayOutputStream
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A speedy implementation of ByteArrayOutputStream. It's not synchronized, and it
 * does not copy buffers when it's expanded. There's also no copying of the internal buffer
 * if it's contents is extracted with the writeTo(stream) method.
 *
 * @author Rickard �berg
 * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
 * @version $Revision: 1.2 $
 */
public class FastByteArrayOutputStream extends ByteArrayOutputStream {
    private static final int DEFAULT_BLOCK_SIZE = 8192;

    /** Internal buffer. */
    private byte[] buffer;

    private LinkedList buffers;

    private int index;
    private int size;
    private int blockSize;

    public FastByteArrayOutputStream() {
        this(DEFAULT_BLOCK_SIZE);
    }

    public FastByteArrayOutputStream(int aSize) {
        blockSize = aSize;
        buffer = new byte[blockSize];
    }

    public void writeTo(OutputStream out) throws IOException {
        // check if we have a list of buffers
        if (buffers != null) {
            Iterator iterator = buffers.iterator();
            while (iterator.hasNext()) {
                byte[] bytes = (byte[]) iterator.next();
                out.write(bytes, 0, blockSize);
            }
        }

        // write the internal buffer directly
        out.write(buffer, 0, index);
    }


    public int size() {
        return size + index;
    }

    public byte[] toByteArray() {
        byte[] data = new byte[size()];

        // check if we have a list of buffers
        int pos = 0;
        if (buffers != null) {
            Iterator iterator = buffers.iterator();
            while (iterator.hasNext()) {
                byte[] bytes = (byte[]) iterator.next();
                System.arraycopy(bytes, 0, data, pos, blockSize);
                pos += blockSize;
            }
        }

        // write the internal buffer directly
        System.arraycopy(buffer, 0, data, pos, index);

        return data;
    }

    public void write(int datum) {
        if (index == blockSize) {
            // Create new buffer and store current in linked list
            if (buffers == null)
                buffers = new LinkedList();

            buffers.addLast(buffer);

            buffer = new byte[blockSize];
            size += index;
            index = 0;
        }

        // store the byte
        buffer[index++] = (byte) datum;
    }

    public void write(byte[] data, int offset, int length) {
        if (data == null) {
            throw new NullPointerException();
        }
        else if ((offset < 0) || (offset + length > data.length)
                || (length < 0)) {
            throw new IndexOutOfBoundsException();
        }
        else {
            if (index + length >= blockSize) {
                // Write byte by byte
                // FIXME optimize this to use arraycopy's instead
                for (int i = 0; i < length; i++)
                    write(data[offset + i]);
            }
            else {
                // copy in the subarray
                System.arraycopy(data, offset, buffer, index, length);
                index += length;
            }
        }
    }

    public synchronized void reset() {
        buffer = new byte[blockSize];
        buffers = null;
    }

    public String toString(String enc) throws UnsupportedEncodingException {
        return new String(toByteArray(), enc);
    }

    public String toString() {
        return new String(toByteArray());
    }

    public void flush() throws IOException {
        // does nothing
    }

    public void close() throws IOException {
        // does nothing
    }
}