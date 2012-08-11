package com.opensymphony.module.sitemesh.scalability.secondarystorage;

import java.io.IOException;
import java.io.Writer;

/**
 * This is a mechanism that allows large SiteMesh requests to be spilled out of memory
 * and into secondary storage.  This means that a fixed amount of memory can be used for a SiteMesh
 * request instead of filling till memory is exhausted.
 * <p/>
 * An implementation of this interface should be LAZY.  It should not open its secondary storage
 * until the first char has been written to it.  This will ensure its as cheap as possible
 * IF there is no need to spill into it.
 * <p/>
 * And implementation MUST also assume that it lives for the life of a request.  A new one will be constructed on
 * every request, another reason to be LAZY on file creation say.
 */
public interface SecondaryStorage
{
    /**
     * Returns the number of characters that should be stored in memory before this secondary storage.  If this value is 0 or below then
     * it is in fact disabled.
     *
     * @return the memory limits in force
     */
    public long getMemoryLimitBeforeUse();

    /**
     * @see java.io.Writer#write(int)
     */
    public void write(int c) throws IOException;

    /**
     * @see java.io.Writer#write(char[], int, int)
     */
    public void write(char[] chars, int off, int len) throws IOException;

    /**
     * @see java.io.Writer#write(String, int, int)
     */
    public void write(String str, int off, int len) throws IOException;

    /**
     * @see java.io.Writer#write(String)
     */
    public void write(String str) throws IOException;

    /**
     * This method will close the "secondary storage" and write the captured output
     * to the specified output writer.
     *
     * @param out the output writer to send the stored data
     *
     * @throws IOException           if the data cant be written
     */
    public void writeTo(Writer out) throws IOException;

    /**
     * This allows you to ask the secondary storage to clean up after itself.  A try  / finally would be a good place to put this.
     * <p/>
     * This should be able to be called multiple times because we want to be able to always cleanup in case of exceptions
     * as well an blue sky paths.
     * <p/>
     * Really no exceptions should be propagated from this method.
     */
    public void cleanUp();
}
