/*
 * Title:        BaseFactory
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.factory;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.module.sitemesh.PageParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Base Factory implementation. Provides utility methods for implementation.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.1 $
 */
public abstract class BaseFactory extends Factory {
    /** ServletConfig or FilterConfig. */
    protected Config config = null;

    /**
     * Instance of {@link com.opensymphony.module.sitemesh.DecoratorMapper}.
     * Because it is thread-safe, it can be shared by multiple clients. This
     * is only the last DecoratorMapper in the chain, and all parents will be
     * automatically delegated to it.
     */
    protected DecoratorMapper decoratorMapper = null;

    /** Map that associates content-types with PageParser instances. */
    protected Map pageParsers = null;

    /** Default PageParser to use if none other can be determined. */
    protected PageParser defaultPageParser = null;

    /**
     * Constructor for default implementation of Factory.
     * Should never be called by client. Singleton instance should be
     * obtained instead.
     *
     * @see #getInstance(com.opensymphony.module.sitemesh.Config config)
     */
    protected BaseFactory(Config config) {
        this.config = config;
        clearDecoratorMappers();
        clearParserMappings();
    }

    /** Return instance of DecoratorMapper. */
    public DecoratorMapper getDecoratorMapper() {
        return decoratorMapper;
    }

    /**
     * Create a PageParser suitable for the given content-type.
     *
     * <p>For example, if the supplied parameter is <code>text/html</code>
     * a parser shall be returned that can parse HTML accordingly. Never
     * returns null.</p>
     *
     * @param contentType The MIME content-type of the data to be parsed
     * @return Appropriate <code>PageParser</code> for reading data
     *
     * @associates PageParser
     * @directed
     * @label creates suitable
     */
    public PageParser getPageParser(String contentType) {
        if (pageParsers.containsKey(contentType)) {
            return (PageParser)pageParsers.get(contentType);
        }
        else {
            return defaultPageParser;
        }
    }

    /** Determine whether a Page of given content-type should be parsed or not. */
    public boolean shouldParsePage(String contentType) {
        return pageParsers.containsKey(contentType);
    }

    /** Clear all current DecoratorMappers. */
    protected void clearDecoratorMappers() {
        decoratorMapper = null;
    }

    /** Push new DecoratorMapper onto end of chain. */
    protected void pushDecoratorMapper(String className, Properties properties) {
        try {
            Class decoratorMapperClass = null;
            try {
                decoratorMapperClass = Class.forName(className);
            }
            catch (NoClassDefFoundError e) {
                decoratorMapperClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
            }
            DecoratorMapper newMapper = (DecoratorMapper) decoratorMapperClass.newInstance();
            newMapper.init(config, properties, decoratorMapper);
            decoratorMapper = newMapper;
        }
        catch (ClassNotFoundException e) {
            report("Could not load DecoratorMapper class : " + className, e);
        }
        catch (Exception e) {
            report("Could not initialize DecoratorMapper : " + className, e);
        }
    }

    /** Clear all PageParser mappings. */
    protected void clearParserMappings() {
        defaultPageParser = null;
        pageParsers = new HashMap();
    }

    /**
     * Map new PageParser to given content-type. contentType = null signifies default
     * PageParser for unknown content-types.
     */
    protected void mapParser(String contentType, String className) {
        try {
            PageParser pp = (PageParser)Class.forName(className).newInstance();
            if (contentType == null) {
                defaultPageParser = pp;
            }
            else {
                pageParsers.put(contentType, pp);
            }
        }
        catch (ClassNotFoundException e) {
            report("Could not load PageParser class : " + className, e);
        }
        catch (Exception e) {
            report("Could not instantiate PageParser : " + className, e);
        }
    }
}