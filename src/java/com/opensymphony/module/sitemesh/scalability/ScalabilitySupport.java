package com.opensymphony.module.sitemesh.scalability;

import com.opensymphony.module.sitemesh.scalability.outputlength.OutputLengthObserver;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;

/**
 * This is really an internal interface to give out to SiteMesh methods so
 * that it takes a single parameter and not four parameters.  Its a view of the scalability
 * support as configured by the host application.
 */
public interface ScalabilitySupport
{
    OutputLengthObserver getOutputLengthObserver();

    SecondaryStorage getSecondaryStorage();

    int getInitialBufferSize();

    boolean isMaxOutputLengthExceededThrown();
}
