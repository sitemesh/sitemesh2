/* This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file. */
package com.opensymphony.module.sitemesh.filter;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/**
 * Converts text stored in byte[] to char[] using specified encoding.
 *
 * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
 * @author <a href="mailto:hani@formicary.net">Hani Suleiman</a>
 * @author Joe Walnes
 * @version $Revision: 1.1 $
*/
public class TextEncoder {

    private static final String DEFAULT_ENCODING = System.getProperty("file.encoding");
    private static final boolean JDK14 =
            System.getProperty("java.version").startsWith("1.4")
            || System.getProperty("java.version").startsWith("1.5");

    public char[] encode(byte[] data, String encoding) throws IOException {
        if (encoding == null) {
            encoding = DEFAULT_ENCODING;
        }
        if (JDK14) {
            return get14Buffer(data, encoding);
        } else {
            return get13Buffer(data, encoding);
        }
    }

    private char[] get13Buffer(byte[] data, String encoding) throws IOException {
        CharArrayWriter out = null;
        // Why all this indirection? Because we are being given bytes, and we have to then write
        // them to characters. We need to know the encoding of the characterset that we are creating.
        // The test that verifies this is InlineDecoratorTest (inline/page6.jsp).
        InputStreamReader reader;
        out = new CharArrayWriter();
        if (encoding != null) {
            reader = new InputStreamReader(new ByteArrayInputStream(data), encoding);
        } else {
            reader = new InputStreamReader(new ByteArrayInputStream(data));
        }

        int i;
        while ((i = reader.read()) != -1) {
            out.write(i);
        }
        return out.toCharArray();
    }

    private char[] get14Buffer(byte[] data, String encoding) throws IOException {
        if (!Charset.isSupported(encoding)) throw new IOException("Unsupported encoding " + encoding);
        Charset charset = Charset.forName(encoding);
        CharsetDecoder cd = charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
        int en = (int) (cd.maxCharsPerByte() * data.length);
        char[] ca = new char[en];
        ByteBuffer bb = ByteBuffer.wrap(data);
        CharBuffer cb = CharBuffer.wrap(ca);
        CoderResult cr = cd.decode(bb, cb, true);
        if (!cr.isUnderflow()) {
            cr.throwException();
        }
        cr = cd.flush(cb);
        if (!cr.isUnderflow()) {
            cr.throwException();
        }
        return trim(ca, cb.position());
    }

    private char[] trim(char[] ca, int len) {
        if (len == ca.length) {
            return ca;
        }
        char[] tca = new char[len];
        System.arraycopy(ca, 0, tca, 0, len);
        return tca;
    }

}
