package com.opensymphony.sitemesh;

public interface Decorator {
    void render(Content content, SiteMeshContext context);
}
