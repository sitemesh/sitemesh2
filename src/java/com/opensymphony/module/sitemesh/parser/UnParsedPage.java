/*
 * Title:        UnParsedPage
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.parser;

import java.io.Writer;

/**
 * A page that is of unrecognised content-type, or cannot be parsed into
 * a specific type of Page.
 *
 * <p>The original page is contained within, but no meta-data or parsed chunks.</p>
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.1 $
 *
 * @see com.opensymphony.module.sitemesh.Page
 */
public final class UnParsedPage extends AbstractPage {
    /**
     * Simple constructor.
     *
     * @param original Original data of page.
     */
    public UnParsedPage(char[] original) {
        this.pageData = original;
    }

    public void writeBody(Writer out) { }
}