package com.opensymphony.module.sitemesh.scalability;

import com.opensymphony.module.sitemesh.factory.FactoryException;
import com.opensymphony.module.sitemesh.scalability.outputlength.NoopOutputLengthObserver;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.NoopSecondaryStorage;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.TempDirSecondaryStorage;
import junit.framework.TestCase;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class ScalabilitySupportConfigurationTest extends TestCase
{

    private Map<String, String> configProperties;

    protected void setUp() throws Exception
    {
        super.setUp();
        configProperties = new HashMap<String, String>();
    }

    public void testNoParams() throws Exception
    {
        ScalabilitySupportConfiguration factory = new ScalabilitySupportConfiguration(new MockFilterConfig(configProperties));
        ScalabilitySupport scalabilitySupport = factory.getScalabilitySupport(new MockHttpServletRequest());

        // this is the case where SiteMesh will act as it always did
        assertTrue(scalabilitySupport.getOutputLengthObserver() instanceof NoopOutputLengthObserver);
        assertTrue(scalabilitySupport.getSecondaryStorage() instanceof NoopSecondaryStorage);
    }

    public void testBadClassSpecified() throws Exception
    {
        configProperties.put("scalability.support.factory.class", "org.rubbish.Rubbish");

        try
        {
            new ScalabilitySupportConfiguration(new MockFilterConfig(configProperties));
            fail("Should not be able to load this class");
        }
        catch (FactoryException expected)
        {
        }

        configProperties.put("scalability.support.factory.class", "java.lang.String");
        try
        {
            new ScalabilitySupportConfiguration(new MockFilterConfig(configProperties));
            fail("Should not be able to use this class");
        }
        catch (FactoryException expected)
        {
        }
    }

    public void testParams() throws Exception
    {
        configProperties.put("scalability.secondarystorage.limit", "40");
        configProperties.put("scalability.maxoutput.length", "60");
        configProperties.put("scalability.maxoutput.httpcode", "555");
        configProperties.put("scalability.maxoutput.throw.exception", "true");

        ScalabilitySupportConfiguration factory = new ScalabilitySupportConfiguration(new MockFilterConfig(configProperties));
        assertEquals(40L, factory.getScalabilitySupportFactory().getSecondaryStorageLimit());
        assertEquals(60L, factory.getScalabilitySupportFactory().getMaximumOutputLength());
        assertEquals(555, factory.getScalabilitySupportFactory().getMaximumOutputExceededHttpCode());
        assertEquals(true, factory.getScalabilitySupportFactory().isMaxOutputLengthExceededThrown());
    }

    public void testClassFactory() throws Exception
    {
        configProperties.put("scalability.support.factory.class", MockScalabilitySupportFactory.class.getName());

        ScalabilitySupportConfiguration factory = new ScalabilitySupportConfiguration(new MockFilterConfig(configProperties));
        ScalabilitySupportFactory scalabilitySupportFactory = factory.getScalabilitySupportFactory();
        assertEquals(5L, scalabilitySupportFactory.getMaximumOutputLength());
        assertEquals(666, scalabilitySupportFactory.getMaximumOutputExceededHttpCode());
        assertEquals(999, scalabilitySupportFactory.getSecondaryStorageLimit());
        assertEquals(false, scalabilitySupportFactory.isMaxOutputLengthExceededThrown());

        assertTrue(scalabilitySupportFactory.hasCustomSecondaryStorage());

        SecondaryStorage secondaryStorage = scalabilitySupportFactory.getSecondaryStorage(new MockHttpServletRequest());
        assertTrue(secondaryStorage instanceof TempDirSecondaryStorage);
    }

    static class MockScalabilitySupportFactory implements ScalabilitySupportFactory
    {
        public int getInitialBufferSize()
        {
            return 4 * 1024;
        }

        public long getMaximumOutputLength()
        {
            return 5;
        }

        public int getMaximumOutputExceededHttpCode()
        {
            return 666;
        }

        public long getSecondaryStorageLimit()
        {
            return 999;
        }

        public boolean isMaxOutputLengthExceededThrown()
        {
            return false;
        }

        public boolean hasCustomSecondaryStorage()
        {
            return true;
        }

        public SecondaryStorage getSecondaryStorage(HttpServletRequest httpServletRequest)
        {
            return new TempDirSecondaryStorage(getSecondaryStorageLimit());
        }
    }

}
