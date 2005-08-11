package com.opensymphony.sitemesh;

public interface DecoratorSelector {
    Decorator selectDecorator(Content content, SiteMeshContext context);
}
