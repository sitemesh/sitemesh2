package com.opensymphony.module.sitemesh.scalability;

import com.opensymphony.module.sitemesh.scalability.outputlength.NoopOutputLengthObserver;
import com.opensymphony.module.sitemesh.scalability.outputlength.OutputLengthObserver;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.NoopSecondaryStorage;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;

/**
 */
public class NoopScalabilitySupport implements ScalabilitySupport
{
    private OutputLengthObserver outputLengthObserver = new NoopOutputLengthObserver();
    private SecondaryStorage secondaryStorage = new NoopSecondaryStorage();

    public OutputLengthObserver getOutputLengthObserver()
    {
        return outputLengthObserver;
    }

    public SecondaryStorage getSecondaryStorage()
    {
        return secondaryStorage;
    }

    public int getInitialBufferSize()
    {
        return 8 * 1024;
    }

    public boolean isMaxOutputLengthExceededThrown()
    {
        return false;
    }
}
