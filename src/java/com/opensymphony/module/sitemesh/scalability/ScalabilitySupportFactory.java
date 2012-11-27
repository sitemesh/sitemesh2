package com.opensymphony.module.sitemesh.scalability;

import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;

import javax.servlet.http.HttpServletRequest;

/**
 * A host application can provide an instance of this interface that provides configuration
 * and also create instances of {@link ScalabilitySupport}
 */
public interface ScalabilitySupportFactory
{
    /**
     * @return the initial amount of memory to allocate to the SiteMesh buffer.  This should be the sweet spot of average page size
     * to prevent excessive re-allocation.
     */
    int getInitialBufferSize();

    /**
     * @return the maximum output that SiteMesh will produce.  If this is 0 or less then
     *         no maximum is in play
     */
    long getMaximumOutputLength();

    /**
     * @return the HTTP code to return if the maximum output is exceeded
     */
    int getMaximumOutputExceededHttpCode();

    /**
     * @return if this is true then the code will throw a {@link com.opensymphony.module.sitemesh.scalability.outputlength.MaxOutputLengthExceeded}
     * if the maximum output length is exceeded instead of sending back {@link #getMaximumOutputExceededHttpCode()}
     */
    boolean isMaxOutputLengthExceededThrown();

    /**
     * @return the number of bytes to keep in memory before secondary storage is usd.  If this is 0 or below then no secondary storage
     *         will be used
     */
    long getSecondaryStorageLimit();

    /**
     * @return if this produces a custom SecondaryStorage object
     */
    boolean hasCustomSecondaryStorage();

    /**
     * If {@link #hasCustomSecondaryStorage()} returns true, then this method will be called to get a custom implementation
     * of SecondaryStorage from the host application.  This method will be invoked for every request if used.
     *
     * @return a custom secondary storage implementation
     */
    SecondaryStorage getSecondaryStorage(final HttpServletRequest httpServletRequest);
}
