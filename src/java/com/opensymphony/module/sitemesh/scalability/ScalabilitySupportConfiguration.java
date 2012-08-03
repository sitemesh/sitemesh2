package com.opensymphony.module.sitemesh.scalability;

import com.opensymphony.module.sitemesh.factory.FactoryException;
import com.opensymphony.module.sitemesh.factory.FilterConfigParameterFactory;
import com.opensymphony.module.sitemesh.scalability.outputlength.ExceptionThrowingOutputLengthObserver;
import com.opensymphony.module.sitemesh.scalability.outputlength.NoopOutputLengthObserver;
import com.opensymphony.module.sitemesh.scalability.outputlength.OutputLengthObserver;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.TempDirSecondaryStorage;
import com.opensymphony.module.sitemesh.util.ClassLoaderUtil;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;

/**
 * A factory to give out scalability objects
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
     * This default implementation will delegate to the hos provided factory if there is one and otherwise read filterConfig parameters
     * for the default values.
     */
    class DefaultScalabilitySupportFactory implements ScalabilitySupportFactory
    {
        private final long secondaryStorageLimit;
        private final long maxOutputLength;
        private final int maximumOutputExceededHttpCode;
        private final ScalabilitySupportFactory hostProvidedFactory;

        DefaultScalabilitySupportFactory(ScalabilitySupportFactory hostProvidedFactory)
        {
            this.hostProvidedFactory = hostProvidedFactory;
            if (hostProvidedFactory == null)
            {
                secondaryStorageLimit = getLongVal("scalability.secondarystorage.limit", -1);
                maxOutputLength = getLongVal("scalability.maxoutput.length", -1);
                maximumOutputExceededHttpCode = getIntVal("scalability.maxoutput.httpcode", 509);
            }
            else
            {
                secondaryStorageLimit = 0;
                maxOutputLength = 0;
                maximumOutputExceededHttpCode = 0;
            }
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

        public boolean hasCustomSecondaryStorage()
        {
            return true;
        }

        public SecondaryStorage getSecondaryStorage(HttpServletRequest httpServletRequest)
        {
            if (hostProvidedFactory != null && hostProvidedFactory.hasCustomSecondaryStorage())
            {
                return hostProvidedFactory.getSecondaryStorage(httpServletRequest);
            }
            else
            {
                return getDefaultSecondaryStorageImpl(httpServletRequest);
            }
        }

        private SecondaryStorage getDefaultSecondaryStorageImpl(HttpServletRequest request)
        {
            // TODO - remove this hack which is just there for demo purposes.  Its a DOS angle kinda sorta??
            boolean siteMeshSecondaryStorage = getRequestFlag(request, "siteMeshSecondaryStorage", true);

            if (siteMeshSecondaryStorage && secondaryStorageLimit > 0)
            {
                request.setAttribute("sitemesh.secondaryStorageLimit", secondaryStorageLimit);
                return new TempDirSecondaryStorage(secondaryStorageLimit);
            }
            else
            {
                request.setAttribute("sitemesh.secondaryStorageLimit", -1L);
                return new com.opensymphony.module.sitemesh.scalability.secondarystorage.NoopSecondaryStorage();
            }
        }

        private boolean getRequestFlag(HttpServletRequest request, final String name, final boolean defaultVal)
        {
            String val = request.getParameter(name);
            if (val == null)
            {
                return defaultVal;
            }
            return Boolean.parseBoolean(val);
        }

    }
}
