/*
 * Title:        DefaultPageParser
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;

/**
 * Default implementation of PageParser - returns an UnParsedPage.
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.1 $
 *
 * @see com.opensymphony.module.sitemesh.PageParser
 */
public class DefaultPageParser implements PageParser {
    public Page parse(char[] data) {
        return new UnParsedPage(data);
    }
}