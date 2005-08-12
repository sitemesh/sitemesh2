package com.opensymphony.sitemesh;

/**
 * Selects an appropriate Decorator for the Content.
 * <p/>
 * Note: Since SiteMesh 3, this replaces the DecoratorMapper.
 *
 * @author Joe Walnes
 * @since SiteMesh 3
 */
public interface DecoratorSelector {

    Decorator selectDecorator(Content content, SiteMeshContext context);

}
