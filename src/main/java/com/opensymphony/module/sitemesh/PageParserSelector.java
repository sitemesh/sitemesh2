package com.opensymphony.module.sitemesh;

/**
 * @author Joe Walnes
 */
public interface PageParserSelector {

    /**
     * Determine whether a Page of given content-type should be parsed or not.
     */
    boolean shouldParsePage(String contentType);

    /**
     * Create a PageParser suitable for the given content-type.
     *
     * <p>For example, if the supplied parameter is <code>text/html</code>
     * a parser shall be returned that can parse HTML accordingly.</p> Never returns null.
     *
     * @param contentType The MIME content-type of the data to be parsed
     * @return Appropriate <code>PageParser</code> for reading data
     *
     */
    PageParser getPageParser(String contentType);

}
