package com.opensymphony.module.sitemesh.scalability.secondarystorage;

import java.io.*;

/**
 * A SecondaryStorage implementation that uses the File.createTempFile() method
 */
public class TempDirSecondaryStorage implements SecondaryStorage
{
    private final long memoryLimitBeforeUse;

    private PrintWriter pw;
    private Reader reader;
    private File tempFile;

    public TempDirSecondaryStorage(long memoryLimitBeforeUse)
    {
        this.memoryLimitBeforeUse = memoryLimitBeforeUse;
    }

    public long getMemoryLimitBeforeUse()
    {
        return memoryLimitBeforeUse;
    }

    private void ensureIsOpen()
    {
        if (pw == null)
        {
            try
            {
                tempFile = getTempFile();
                pw = new PrintWriter(new FileWriter(tempFile));
            }
            catch (IOException e)
            {
                throw new RuntimeException("Unable to create SiteMesh secondary storage in temp directory");
            }
        }
    }

    protected File getTempFile() throws IOException
    {
        return File.createTempFile("sitemesh-spillover-@" + memoryLimitBeforeUse + "-", ".txt");
    }

    public void write(int c)
    {
        ensureIsOpen();
        pw.write(c);
    }

    public void write(char[] chars, int off, int len)
    {
        ensureIsOpen();
        pw.write(chars, off, len);
    }

    public void write(String str, int off, int len)
    {
        ensureIsOpen();
        pw.write(str, off, len);
    }

    public void write(String str)
    {
        ensureIsOpen();
        pw.write(str);
    }

    public Reader readBack()
    {
        if (reader != null)
        {
            throw new IllegalStateException("You have asked for the SecondaryStorage reader twice.  You cant read streamed data twice");
        }
        if (pw == null)
        {
            // it was never written to so give them no content back
            reader = new StringReader("");
        }
        else
        {
            pw.close();
            pw = null;
            try
            {
                reader = new BufferedReader(new FileReader(tempFile));
            }
            catch (FileNotFoundException e)
            {
                throw new RuntimeException("Unable to read temporary SiteMesh storage file " + tempFile, e);
            }
        }
        return reader;
    }

    public void close()
    {
        try
        {
            if (pw != null) {
                pw.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (tempFile != null)
            {
                tempFile.delete();
            }
        }
        catch (IOException e)
        {
        }
        pw = null;
        reader = null;
    }
}
