package com.opensymphony.module.sitemesh.util;

import java.io.*;

/**
 * A converter from one character type to another
 * <p>
 * This class is not threadsafe
 */
public class OutputConverter
{
    /**
     * Resin versions 2.1.12 and previously have encoding problems for internationalisation.
     * <p>
     * This is fixed in Resin 2.1.13.  Once 2.1.13 is used more widely, this parameter (and class) can be removed.
     */
    public static final String WORK_AROUND_RESIN_I18N_BUG = "sitemesh.resin.i18n.workaround";

    public static Writer getWriter(Writer out)
    {
        if ("true".equalsIgnoreCase(System.getProperty(WORK_AROUND_RESIN_I18N_BUG)))
            return new ResinWriter(out);
        else
            return out;
    }

    public static String convert(String inputString) throws IOException
    {
        if ("true".equalsIgnoreCase(System.getProperty(WORK_AROUND_RESIN_I18N_BUG)))
        {
            StringWriter sr = new StringWriter();
            resinConvert(inputString, sr);
            return sr.getBuffer().toString();
        }
        else
            return inputString;
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
            resinConvert(buffer.toString(), target);
            buffer.reset();
        }

        public void write(char cbuf[], int off, int len) throws IOException
        {
            buffer.write(cbuf, off, len);
            flush();
        }
    }

    private static void resinConvert(String inputString, Writer writer) throws IOException
    {
        //does this need to be made configurable?  Or are these two always correct?
        InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(inputString.getBytes("UTF-8")), "ISO-8859-1");
        int i;
        while ((i = reader.read()) != -1)
        {
            writer.write(i);
        }
        writer.flush();
    }

}
