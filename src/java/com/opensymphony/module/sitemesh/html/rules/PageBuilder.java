package com.opensymphony.module.sitemesh.html.rules;

/**
 * Allows a TagRule to add information to a Page object.
 *
 * The standard HTML processing rules bundled with SiteMesh use this interface instead of direct coupling to the HTMLPage
 * class, allowing the rules to be used for HTML processing in applications outside of SiteMesh.
 *
 * @author Joe Walnes
 */
public interface PageBuilder {
    void addProperty(String key, String value);
}
