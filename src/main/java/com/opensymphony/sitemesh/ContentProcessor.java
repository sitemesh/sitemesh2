package com.opensymphony.sitemesh;

import java.io.IOException;

/**
 * @author Joe Walnes
 * @since SiteMesh 3
 */
public interface ContentProcessor {

    boolean handles(SiteMeshContext context);
    boolean handles(String contentType);

    Content build(char[] data, SiteMeshContext context) throws IOException;
}
