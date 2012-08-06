package com.opensymphony.module.sitemesh.scalability.secondarystorage;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A SecondaryStorage implementation that uses the File.createTempFile() method
 */
public class TempDirSecondaryStorage implements SecondaryStorage
{
    protected static Logger logger = Logger.getLogger(TempDirSecondaryStorage.class.getName());

    private final long memoryLimitBeforeUse;

    private PrintWriter pw;
    private Reader reader;
    private File tempFile;
    private File tempDirectory;

    public TempDirSecondaryStorage(long memoryLimitBeforeUse)
    {
        this(memoryLimitBeforeUse,null);
    }

    public TempDirSecondaryStorage(long memoryLimitBeforeUse, File tempDirectory)
    {
        this.memoryLimitBeforeUse = memoryLimitBeforeUse;
        this.tempDirectory = tempDirectory;
    }

    public long getMemoryLimitBeforeUse()
    {
        return memoryLimitBeforeUse;
    }

    public File getTempDirectory()
    {
        return tempDirectory;
    }

    protected void ensureIsOpen()
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

    /**
     * By default creates a temporary file in {@link #getTempDirectory()}.  The Java {@link File#createTempFile(String, String, java.io.File)} ensures
     * that the file is unique.
     *
     * @return a write-able temporary file
     * @throws IOException
     */
    protected File getTempFile() throws IOException
    {
        return File.createTempFile("sitemesh-spillover-@" + memoryLimitBeforeUse + "-", ".txt", tempDirectory);
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

    public void cleanUp()
    {
        try
        {
            cleanupImplementation();
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE,"Unable to clean up SiteMesh secondary storage",e);
        }
        pw = null;
        reader = null;
    }

    protected void cleanupImplementation() throws IOException
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
}
