package com.opensymphony.module.sitemesh.scalability.secondarystorage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A SecondaryStorage implementation that uses the File.createTempFile() method
 */
public class TempDirSecondaryStorage implements SecondaryStorage
{
    protected static Logger logger = Logger.getLogger(TempDirSecondaryStorage.class.getName());
    private static final String UTF_8 = "UTF-8";

    private final long memoryLimitBeforeUse;

    private Writer captureWriter;
    private File tempFile;
    private File tempDirectory;

    public TempDirSecondaryStorage(long memoryLimitBeforeUse)
    {
        this(memoryLimitBeforeUse, null);
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
        if (captureWriter == null)
        {
            try
            {
                tempFile = getTempFile();
                captureWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8"));
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
     *
     * @throws IOException
     */
    protected File getTempFile() throws IOException
    {
        return File.createTempFile("sitemesh-spillover-" + memoryLimitBeforeUse + "-", ".txt", tempDirectory);
    }

    public void write(int c) throws IOException
    {
        ensureIsOpen();
        captureWriter.write(c);
    }

    public void write(char[] chars, int off, int len) throws IOException
    {
        ensureIsOpen();
        captureWriter.write(chars, off, len);
    }

    public void write(String str, int off, int len) throws IOException
    {
        ensureIsOpen();
        captureWriter.write(str, off, len);
    }

    public void write(String str) throws IOException
    {
        ensureIsOpen();
        captureWriter.write(str);
    }

    public void writeTo(Writer out) throws IOException
    {
        if (captureWriter != null)
        {
            captureWriter.close();
            captureWriter = null;
            Reader reader = null;
            try
            {
                reader = new InputStreamReader(new FileInputStream(tempFile), UTF_8);
                int read;
                char temp[] = new char[8192];
                while ((read = reader.read(temp)) != -1)
                {
                    out.write(temp, 0, read);
                }
            }
            catch (FileNotFoundException e)
            {
                throw new RuntimeException("Unable to open temporary SiteMesh storage file " + tempFile, e);
            }
            finally
            {
                if (reader != null)
                {
                    reader.close();
                }
            }
        }
    }

    public void cleanUp()
    {
        try
        {
            cleanupImplementation();
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Unable to clean up SiteMesh secondary storage", e);
        }
        captureWriter = null;
    }

    protected void cleanupImplementation() throws IOException
    {
        if (captureWriter != null)
        {
            captureWriter.close();
        }
        if (tempFile != null)
        {
            tempFile.delete();
        }
    }
}
