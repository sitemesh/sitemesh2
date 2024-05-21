package com.opensymphony.sitemesh.webapp;

import com.opensymphony.module.sitemesh.util.Container;

/**
 * Provides details of which tweaks should be used in SiteMesh - necessary because containers behave subtly different.
 *
 * @author Joe Walnes
 * @since SiteMesh 3
 */
public class ContainerTweaks {

    // TODO: Externalize these into a config file (optional of course!), allowing users to change them at runtime if needed.

    private final int container = Container.get();

    public boolean shouldAutoCreateSession() {
        return false;
//        return container == Container.TOMCAT;  - this is removed due to SIM-151.  
    }

    public boolean shouldLogUnhandledExceptions() {
        return container == Container.TOMCAT;
    }

    public boolean shouldIgnoreIllegalStateExceptionOnErrorPage() {
        return container == Container.WEBLOGIC;
    }
}
