package com.opensymphony.sitemesh.content;

import com.opensymphony.sitemesh.SiteMeshContext;
import com.opensymphony.sitemesh.Content;

public interface ContentProcessor {

    boolean handles(SiteMeshContext context);
    boolean handles(String contentType);

    Content build(char[] data);
}
