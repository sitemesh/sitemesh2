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
import javax.servlet.ServletResponse;
import java.io.*;
import java.nio.charset.*;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;

/**
 * Implementation of ServletOutputStream that stores all data written
 * to it in a temporary buffer accessible from {@link #getBuffer(java.lang.String)} .
 *
 * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
 * @author <a href="mailto:hani@formicary.net">Hani Suleiman</a>
 * @version $Revision: 1.4 $
 */
public class PageOutputStream extends ServletOutputStream implements OutputBuffer {

    private ByteArrayOutputStream buffer = null;
    private OutputStream target = null;
    private static final String DEFAULT_ENCODING = System.getProperty("file.encoding");
    private static final boolean JDK14 = System.getProperty("java.version").startsWith("1.4");

    /**
     * The reason we use a response, rather than just getting the outputstream directly is that under Tomcat, when
     * serving static resources, we need to avoid calling getOutputStream() if we are going to use a decorator.
     * <p>
     * More information: http://marc.theaimsgroup.com/?l=tomcat-user&m=107569601410973&w=2, SIM-74, SIM-82
     */
    private final ServletResponse response;

    /**
     * Construct an OutputStream that will buffer the content written to it.
     */
    public PageOutputStream(ServletResponse response) {
        this.response = response;
        this.buffer = new FastByteArrayOutputStream();
        target = buffer;
    }

    public void write(int b) throws IOException {
        target.write(b);
    }

    public void discardBuffer() {
        //allow multiple calls to this method without duplicating content to target
        if (target == buffer) {
            try {
                OutputStream original = response.getOutputStream();
                target = original;
                // copy any bytes written to buffer so far to original stream.
                original.write(buffer.toByteArray());
                original.flush();
            } catch (IOException e) {
                e.printStackTrace(); // this should never happen.
            }
        }
    }

  public char[] get14Buffer(String encoding) {
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

    public char[] getBuffer(String encoding) {
      if(JDK14) return get14Buffer(encoding);
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

            int i;
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

}