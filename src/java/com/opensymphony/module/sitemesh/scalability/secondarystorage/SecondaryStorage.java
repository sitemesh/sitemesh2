package com.opensymphony.module.sitemesh.scalability.secondarystorage;

import java.io.Reader;

/**
 * This is a mechanism that allows large SiteMesh requests to be spilled out of memory
 * and into secondary storage.  This means that a fixed amount of memory can be used for a SiteMesh
 * request instead of filling till memory is exhausted.
 *
 * An implementation of this interface should be LAZY.  It should not open its secondary storage
 * until the first char has been written to it.  This will ensure its as cheap as possible
 * IF there is no need to spill into it.
 *
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
    public void write(int c);

    /**
     * @see java.io.Writer#write(char[], int, int)
     */
    public void write(char[] chars, int off, int len);

    /**
     * @see java.io.Writer#write(String, int, int)
     */
    public void write(String str, int off, int len);

    /**
     * @see java.io.Writer#write(String)
     */
    public void write(String str);

    /**
     * This method will close the "output" and construct an input reader so that the contents
     * can be read back.
     *
     * @return a reader that is the contents that was previously captured.
     * @throws IllegalStateException if this method has already been called.  You cant ask for streamed data twice.
     */
    public Reader readBack();

    /**
     * This allows you to ask the secondary storage to clean up after itself.  A try  / finally would be a good place to put this
     */
    public void close();
}
