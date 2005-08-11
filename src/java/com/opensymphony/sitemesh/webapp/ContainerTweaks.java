package com.opensymphony.sitemesh.webapp;

import com.opensymphony.module.sitemesh.util.Container;

public class ContainerTweaks {

    private final int container = Container.get();

    public boolean shouldAutoCreateSession() {
        return container == Container.TOMCAT;
    }

    public boolean shouldLogUnhandledExceptions() {
        return container == Container.TOMCAT;
    }
}
