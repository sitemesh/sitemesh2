package com.opensymphony.module.sitemesh.scalability.secondarystorage;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

/**
 */
public class TempDirSecondaryStorageTest extends TestCase
{

    public static final String THE_QUICK_BROWN_FOX_JUMPED_OVER_THE_LAZY_DOG = "The quick brown fox jumped over the lazy dog";
    private File tempFile;

    protected void setUp() throws Exception
    {
        super.setUp();
        tempFile = File.createTempFile("TempDirSecondaryStorageTest", ".txt");
        tempFile.deleteOnExit();
    }

    @Override
    public void tearDown() throws Exception
    {
        tempFile.delete();
    }

    public void testDataInDataOut() throws Exception
    {
        TempDirSecondaryStorage storage = new TempDirSecondaryStorage(5)
        {
            @Override
            protected File getTempFile() throws IOException
            {
                return tempFile;
            }
        };

        storage.write(THE_QUICK_BROWN_FOX_JUMPED_OVER_THE_LAZY_DOG);

        Reader reader = storage.readBack();
        assertReader(reader,THE_QUICK_BROWN_FOX_JUMPED_OVER_THE_LAZY_DOG);


        // check that after close the file has been closed and deleted
        assertTrue(tempFile.exists());

        storage.close();

        assertFalse(tempFile.exists());
        assertClosed(reader);
    }

    private void assertReader(Reader reader, String expected) throws IOException
    {
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        assertEquals(expected,line);
    }

    private void assertClosed(Reader reader)
    {
        try
        {
            reader.read();
            fail("Should have thrown IOF as its already closed");
        }
        catch (IOException expected)
        {
        }
    }
}
