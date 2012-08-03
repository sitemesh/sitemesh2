package com.opensymphony.module.sitemesh.scalability;

import com.opensymphony.module.sitemesh.scalability.outputlength.OutputLengthObserver;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;

/**
 */
public interface ScalabilitySupport
{
    OutputLengthObserver getOutputLengthObserver();

    SecondaryStorage getSecondaryStorage();
}
