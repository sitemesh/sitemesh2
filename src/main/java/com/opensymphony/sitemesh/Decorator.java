package com.opensymphony.sitemesh;

/**
 * @author Joe Walnes
 * @since SiteMesh 3
 */
public interface Decorator {
    void render(Content content, SiteMeshContext context);
}
