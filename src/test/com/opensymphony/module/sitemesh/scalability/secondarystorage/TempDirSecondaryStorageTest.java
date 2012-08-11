package com.opensymphony.module.sitemesh.scalability.secondarystorage;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

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

        StringWriter out = new StringWriter();
        storage.writeTo(out);
        assertData(out.toString(), THE_QUICK_BROWN_FOX_JUMPED_OVER_THE_LAZY_DOG);


        // check that after close the file has been closed and deleted
        assertTrue(tempFile.exists());

        storage.cleanUp();

        assertFalse(tempFile.exists());
    }

    private void assertData(String result, String expected) throws IOException
    {
        assertEquals(expected,result);
    }
}
