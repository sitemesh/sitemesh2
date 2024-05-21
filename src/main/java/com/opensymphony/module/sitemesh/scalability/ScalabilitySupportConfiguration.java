package com.opensymphony.module.sitemesh.scalability;

import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.module.sitemesh.factory.FactoryException;
import com.opensymphony.module.sitemesh.factory.FilterConfigParameterFactory;
import com.opensymphony.module.sitemesh.scalability.outputlength.ExceptionThrowingOutputLengthObserver;
import com.opensymphony.module.sitemesh.scalability.outputlength.NoopOutputLengthObserver;
import com.opensymphony.module.sitemesh.scalability.outputlength.OutputLengthObserver;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.NoopSecondaryStorage;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.TempDirSecondaryStorage;
import com.opensymphony.module.sitemesh.util.ClassLoaderUtil;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;

/**
 * The factory class to give out scalability objects
 */
public class ScalabilitySupportConfiguration extends FilterConfigParameterFactory
{
    private final ScalabilitySupportFactory scalabilitySupportFactory;

    public ScalabilitySupportConfiguration(FilterConfig filterConfig)
    {
        super(filterConfig);
        String scalabilitySupportFactoryClass = getStringVal("scalability.support.factory.class", null);

        ScalabilitySupportFactory hostProvided = null;
        if (scalabilitySupportFactoryClass != null)
        {
            try
            {
                hostProvided = (ScalabilitySupportFactory) ClassLoaderUtil.loadClass(scalabilitySupportFactoryClass, getClass()).newInstance();
            }
            catch (ClassNotFoundException e)
            {
                throw new FactoryException("Could not load SecondaryStorageFactory class : " + scalabilitySupportFactoryClass, e);
            }
            catch (Exception e)
            {
                throw new FactoryException("Could not instantiate SecondaryStorageFactory class : " + scalabilitySupportFactoryClass, e);
            }

        }
        scalabilitySupportFactory = new DefaultScalabilitySupportFactory(hostProvided);
    }

    public ScalabilitySupport getScalabilitySupport(final HttpServletRequest httpServletRequest)
    {
        final SecondaryStorage secondaryStorage = scalabilitySupportFactory.getSecondaryStorage(httpServletRequest);
        final OutputLengthObserver outputLengthObserver = getOutputLengthObserver();
        final int initialBufferSize = scalabilitySupportFactory.getInitialBufferSize();
        final boolean isMaxOutputLengthExceededThrown = scalabilitySupportFactory.isMaxOutputLengthExceededThrown();
        return new ScalabilitySupport()
        {
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
                return initialBufferSize;
            }

            public boolean isMaxOutputLengthExceededThrown()
            {
                return isMaxOutputLengthExceededThrown;
            }
        };
    }

    // visible for testing
    ScalabilitySupportFactory getScalabilitySupportFactory()
    {
        return scalabilitySupportFactory;
    }

    private OutputLengthObserver getOutputLengthObserver()
    {
        if (scalabilitySupportFactory.getMaximumOutputLength() > 0)
        {
            return new ExceptionThrowingOutputLengthObserver(scalabilitySupportFactory.getMaximumOutputLength(), scalabilitySupportFactory.getMaximumOutputExceededHttpCode());
        }
        else
        {
            return new NoopOutputLengthObserver();
        }
    }

    /**
     * This default implementation will delegate to the host provided factory if there is one and otherwise read filterConfig parameters
     * for the default values.
     */
    class DefaultScalabilitySupportFactory implements ScalabilitySupportFactory
    {

        private final long secondaryStorageLimit;
        private final long maxOutputLength;
        private final int maximumOutputExceededHttpCode;
        private final ScalabilitySupportFactory hostProvidedFactory;
        private final int initialBufferSize;
        private final boolean throwsException;

        DefaultScalabilitySupportFactory(ScalabilitySupportFactory hostProvidedFactory)
        {
            this.hostProvidedFactory = hostProvidedFactory;
            if (hostProvidedFactory == null)
            {
                maxOutputLength = longVal("scalability.maxoutput.length", -1);
                throwsException = booleanVal("scalability.maxoutput.throw.exception", true);
                maximumOutputExceededHttpCode = intVal("scalability.maxoutput.httpcode", 509);
                secondaryStorageLimit = longVal("scalability.secondarystorage.limit", -1);
                initialBufferSize = intVal("scalability.initial.buffer.size", 8 * 1024);
            }
            else
            {
                secondaryStorageLimit = 0;
                maxOutputLength = 0;
                maximumOutputExceededHttpCode = 0;
                initialBufferSize = 0;
                throwsException = true;
            }
        }

        public int getInitialBufferSize()
        {
            return hostProvidedFactory != null ? hostProvidedFactory.getInitialBufferSize() : initialBufferSize;
        }

        public long getMaximumOutputLength()
        {
            return hostProvidedFactory != null ? hostProvidedFactory.getMaximumOutputLength() : maxOutputLength;
        }

        public int getMaximumOutputExceededHttpCode()
        {
            return hostProvidedFactory != null ? hostProvidedFactory.getMaximumOutputExceededHttpCode() : maximumOutputExceededHttpCode;
        }

        public long getSecondaryStorageLimit()
        {
            return hostProvidedFactory != null ? hostProvidedFactory.getSecondaryStorageLimit() : secondaryStorageLimit;
        }

        public boolean isMaxOutputLengthExceededThrown()
        {
            return hostProvidedFactory != null ? hostProvidedFactory.isMaxOutputLengthExceededThrown() :  throwsException;
        }

        public boolean hasCustomSecondaryStorage()
        {
            return true;
        }

        public SecondaryStorage getSecondaryStorage(HttpServletRequest httpServletRequest)
        {
            httpServletRequest.setAttribute(RequestConstants.SECONDARY_STORAGE_LIMIT, secondaryStorageLimit);
            if (hostProvidedFactory != null && hostProvidedFactory.hasCustomSecondaryStorage())
            {
                return hostProvidedFactory.getSecondaryStorage(httpServletRequest);
            }
            else
            {
                if (secondaryStorageLimit > 0)
                {
                    return new TempDirSecondaryStorage(secondaryStorageLimit);
                }
                else
                {
                    return new NoopSecondaryStorage();
                }
            }
        }

    }
}
