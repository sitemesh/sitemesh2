package com.opensymphony.module.sitemesh.util;

import java.io.*;

/**
 * A converter from one character type to another
 * <p>
 * This class is not threadsafe
 */
public class OutputConverter
{
    public static Writer getWriter(Writer out)
    {
        if (Container.get() == Container.RESIN)
            return new ResinWriter(out);
        else
            return out;
    }

    /**
     * To get internationalised characters to work on Resin, some conversions need to take place.
     */
    static class ResinWriter extends Writer
    {
        private final Writer target;
        private final CharArrayWriter buffer = new CharArrayWriter();

        public ResinWriter(Writer target)
        {
            this.target = target;
        }

        public void close() throws IOException
        {
            flush();
        }

        public void flush() throws IOException
        {
            //does this need to be made configurable?  Or are these two always correct?
            InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(buffer.toString().getBytes("UTF-8")), "ISO-8859-1");
            int i;
            while ((i = reader.read()) != -1) {
                target.write(i);
            }
            target.flush();
            buffer.reset();
        }

        public void write(char cbuf[], int off, int len) throws IOException
        {
            buffer.write(cbuf, off, len);
            flush();
        }
    }
}
