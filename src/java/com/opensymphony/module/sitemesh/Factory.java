/*
 * Title:        Factory
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh;

import com.opensymphony.module.sitemesh.factory.FactoryException;
import com.opensymphony.module.sitemesh.util.Container;

import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import java.lang.reflect.Constructor;

/**
 * Factory responsible for creating appropriate instances of implementations.
 * This is specific to a web context and is obtained through {@link #getInstance(com.opensymphony.module.sitemesh.Config)}.
 *
 * <p>The actual Factory method used is determined by the enviroment entry <code>sitemesh.factory</code>.
 * If this doesn't exist, it defaults to {@link com.opensymphony.module.sitemesh.factory.DefaultFactory} .</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.5 $
 */
public abstract class Factory {
    /** Web context lookup key */
    private static final String SITEMESH_FACTORY = "sitemesh.factory";

    /**
     * Entry-point for obtaining singleton instance of Factory. The default factory class
     * that will be instantiated can be overridden with the environment
     * entry <code>sitemesh.factory</code>.
     */
    public static Factory getInstance(Config config) {
        Factory instance = (Factory)config.getServletContext().getAttribute(SITEMESH_FACTORY);
        if (instance == null) {
            String factoryClass = getEnvEntry("sitemesh.factory", "com.opensymphony.module.sitemesh.factory.DefaultFactory");
            try {
                Class cls;
                try {
                    cls = Class.forName(factoryClass);
                }
                catch (NoClassDefFoundError e) {
                    cls = Class.forName(factoryClass, true, Thread.currentThread().getContextClassLoader());
                }

                Constructor con = cls.getConstructor(new Class[] { Config.class });
                instance = (Factory)con.newInstance(new Config[] { config });
                config.getServletContext().setAttribute(SITEMESH_FACTORY, instance);
            }
            catch (Exception e) {
                report("Cannot construct Factory : " + factoryClass, e);
            }
        }
        return instance;
    }

    /** Return instance of DecoratorMapper. */
    public abstract DecoratorMapper getDecoratorMapper();

    /**
     * Create a PageParser suitable for the given content-type.
     *
     * <p>For example, if the supplied parameter is <code>text/html</code>
     * a parser shall be returned that can parse HTML accordingly.</p> Never returns null.
     *
     * @param contentType The MIME content-type of the data to be parsed
     * @return Appropriate <code>PageParser</code> for reading data
     *
     */
    public abstract PageParser getPageParser(String contentType);

    /** Determine whether a Page of given content-type should be parsed or not. */
    public abstract boolean shouldParsePage(String contentType);

    /**
     * Determine whether the given path should be excluded from decoration or not.
     */
    public abstract boolean isPathExcluded(String path);

    /** Report a problem. */
    protected static void report(String msg, Exception e) {
        throw new FactoryException(msg, e);
    }

    /** Find String environment entry, or return default if not found. */
    private static String getEnvEntry(String envEntry, String defaultValue) {
        String result = null;
        try {
            if (Container.get() != Container.JRUN) {
                // TODO: JRun really isn't happy with this
                InitialContext ctx = new InitialContext();
                Object o = ctx.lookup("java:comp/env/" + envEntry);
                ctx.close();
                result = (String)PortableRemoteObject.narrow(o, String.class); // rmi-iiop friendly.
            }
        }
        catch (Exception e) { } // failed - don't moan, just return default.
        return result == null || result.trim().length() == 0 ? defaultValue : result;
    }
}