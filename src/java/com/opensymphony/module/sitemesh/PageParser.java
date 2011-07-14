/*
 * Title:        PageParser
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh;

import java.io.IOException;

/**
 * The PageParser is responsible for parsing the page data into an appropriate
 * {@link com.opensymphony.module.sitemesh.Page} object.
 *
 * <p>The implementation of this can be switched to parse different kind of data
 * (<i>e.g.</i> HTML, WML, FOP, images) or for performance enhancements. An
 * implementation is obtained through the {@link com.opensymphony.module.sitemesh.Factory} .</p>
 *
 * <p>A single PageParser is reused, therefore the parse() methods need to be thread-safe.</p>
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.2 $
 */
public interface PageParser {

    /**
     * Parse the given buffer into a page object.  {@link DefaultSitemeshBuffer} is the appropriate implementation of
     * this interface to pass in.
     *
     * @param buffer The buffer for the page.
     * @return The parsed page
     * @throws IOException if an error occurs
     */
    Page parse(SitemeshBuffer buffer) throws IOException;

    /**
     * Parse the given buffer into a Page object.
     *
     * @param buffer The buffer for the page.
     * @return The parsed page
     * @throws IOException if an error occurs
     * @deprecated Use {@link PageParser#parse(SitemeshBuffer)}, to allow performance improvement such as single buffer
     *      parsing and buffer chaining.
     */
    @Deprecated
    Page parse(char[] buffer) throws IOException;
}
