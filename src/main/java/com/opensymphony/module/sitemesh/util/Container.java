/*
 * Title:        Container
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Utility for determining the Servlet Container the application is running in.
 * Currently supported containers: Tomcat, Resin, Orion, OC4J, WebLogic, HPAS, JRun,
 * Websphere.
 *
 * <h3>Usage:</h3>
 *
 * <code>if (Container.get() == Container.TOMCAT) { .... }</code>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.2 $
 */
public final class Container {
    public static final int UNKNOWN   = 0;
    public static final int TOMCAT    = 1;
    public static final int RESIN     = 2;
    public static final int ORION     = 3; // Orion or OC4J
    public static final int WEBLOGIC  = 4;
    public static final int HPAS      = 5;
    public static final int JRUN      = 6;
    public static final int WEBSPHERE = 7;

    private static int result = -1;

    /**
     * A map containing classes that can be searched for,
     * and which container they are typically found in.
     */
    private static Map classMappings = null;

    static {
        // initialize the classes that can be searched for
        classMappings = new HashMap(6);
        classMappings.put("org.apache.jasper.runtime.JspFactoryImpl",             new Integer(TOMCAT));
        classMappings.put("com.caucho.jsp.JspServlet",                            new Integer(RESIN));
        classMappings.put("com.evermind.server.http.JSPServlet",                  new Integer(ORION));
        classMappings.put("weblogic.servlet.JSPServlet",                          new Integer(WEBLOGIC));
        classMappings.put("com.hp.mwlabs.j2ee.containers.servlet.jsp.JspServlet", new Integer(HPAS));
        classMappings.put("jrun.servlet.WebApplicationService",                   new Integer(JRUN));
        classMappings.put("com.ibm.ws.webcontainer.jsp.servlet.JspServlet",       new Integer(WEBSPHERE));
    }

    /** Get the current container. */
    public static int get() {
        if (result == -1) {
            final String classMatch = searchForClosestClass(classMappings);

            if (classMatch == null) {
                result = UNKNOWN;
            }
            else {
                result = ((Integer) classMappings.get(classMatch)).intValue();
            }
        }
        return result;
    }

    /**
     * Walk up the classloader hierachy and attempt to find a class in the classMappings Map
     * that can be loaded.
     *
     * @return Name of the match class, or null if not found.
     */
    private static String searchForClosestClass(Map classMappings) {
        // get closest classloader
        ClassLoader loader = Container.class.getClassLoader();

        // iterate up through the classloader hierachy (through parents), until no more left.
        while (loader != null) {

            for (Iterator iterator = classMappings.keySet().iterator(); iterator.hasNext();) {
                String className = (String) iterator.next();

                try {
                    // attempt to load current classname with current classloader
                    loader.loadClass(className);
                    // if no exception has been thrown, we're in luck.
                    return className;
                }
                catch (ClassNotFoundException e) {
                    // no problem... we'll keep trying...
                }
            }
            loader = loader.getParent();
        }

        // couldn't find anything
        return null;
    }
}
