/*
 * Title:        PageOutputStream
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.filter;

import com.opensymphony.module.sitemesh.util.FastByteArrayOutputStream;

import javax.servlet.ServletOutputStream;
import java.io.*;
import java.nio.charset.*;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;

/**
 * Implementation of ServletOutputStream that stores all data written
 * to it in a temporary buffer accessible from {@link #getBuffer()} .
 *
 * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
 * @author <a href="mailto:hani@formicary.net">Hani Suleiman</a>
 * @version $Revision: 1.1 $
 */
public class PageOutputStream extends ServletOutputStream implements OutputBuffer {

    private ByteArrayOutputStream buffer = null;
    private OutputStream original = null;
    private OutputStream target = null;
    private String encoding;
    private static final String DEFAULT_ENCODING = System.getProperty("file.encoding");
    private static final boolean JDK14 = System.getProperty("java.version").startsWith("1.4");

  /**
     * Construct an PageOutputStream around the specified PrintWriter.  If the encoding is
     * null, then the systems default encoding is used.
     *
     * @param original  The underlying outputStream
     * @param encoding  The encoding to use
     */
    public PageOutputStream(OutputStream original, String encoding) {
        this.encoding = encoding;
        this.original = original;
        this.buffer = new FastByteArrayOutputStream();
        target = buffer;
    }

    public void write(int b) throws IOException {
        target.write(b);
    }

    public void discardBuffer() {
        //allow multiple calls to this method without duplicating content to target
        if (target == buffer) {
            target = original;
            try {
                // copy any bytes written to buffer so far to original stream.
                original.write(buffer.toByteArray());
                original.flush();
            } catch (IOException e) {
                e.printStackTrace(); // this should never happen.
            }
        }
    }

  public char[] get14Buffer() {
    String enc = encoding;
    if(enc == null) {
      enc = DEFAULT_ENCODING;
    }
    if(!Charset.isSupported(enc)) throw new InternalError("Unsupported encoding " + enc);
    Charset charset = java.nio.charset.Charset.forName(enc);
    CharsetDecoder cd = charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
    byte[] ba = buffer.toByteArray();
    int en = (int)(cd.maxCharsPerByte() * ba.length);
    char[] ca = new char[en];
    ByteBuffer bb = ByteBuffer.wrap(ba);
    CharBuffer cb = CharBuffer.wrap(ca);
    try {
      CoderResult cr = cd.decode(bb, cb, true);
      if(!cr.isUnderflow())
        cr.throwException();
      cr = cd.flush(cb);
      if(!cr.isUnderflow())
        cr.throwException();
    } catch(Exception x) {
      throw new Error(x);
    }
    return trim(ca, cb.position());
  }

  private static char[] trim(char[] ca, int len)
  {
    if(len == ca.length)
      return ca;
    char[] tca = new char[len];
    System.arraycopy(ca, 0, tca, 0, len);
    return tca;
  }

    public char[] getBuffer() {
      if(JDK14) return get14Buffer();
        CharArrayWriter out = null;
        try {
            // Why all this indirection? Because we are being given bytes, and we have to then write
            // them to characters. We need to know the encoding of the characterset that we are creating.
            // The test that verifies this is InlineDecoratorTest (inline/page6.jsp).
            InputStreamReader reader;
            out = new CharArrayWriter();
            if (encoding != null)
                reader = new InputStreamReader(new ByteArrayInputStream(buffer.toByteArray()), encoding);
            else
                reader = new InputStreamReader(new ByteArrayInputStream(buffer.toByteArray()));

            int i = 0;
            while ((i = reader.read()) != -1) {
                out.write(i);
            }
        } catch (IOException e) {
            e.printStackTrace(); //this should never happen.
        }

        return out.toCharArray();
    }

    public void flush() {
        try {
            target.flush();
        } catch (IOException e) {
            e.printStackTrace(); //not sure really what to do here.
        }
    }

  public static void main(String[] args) throws IOException
  {
    OutputStream bos = new ByteArrayOutputStream();
    String val = "abcdefsdoihsdifhu saiudfg qqwe ";
    val+="\\u0126\\u0118\\u0139\\u0139\\u0150";
    StringBuffer sb = new StringBuffer(1000);
    for(int i=0;i<500;i++)
    {
      sb.append(val);
    }
    bos.write(val.getBytes(DEFAULT_ENCODING));
    PageOutputStream os = new PageOutputStream(bos, DEFAULT_ENCODING);
    int iterations = 10000;
    long[] blah = new long[iterations];
    long now = System.currentTimeMillis();
    for(int i=0;i<iterations;i++)
    {
      char[] chars = os.getBuffer();
      blah[i] = chars.length;
    }
    System.out.println("time taken for buffer=" + (double)(System.currentTimeMillis()-now)/iterations);
  }
}