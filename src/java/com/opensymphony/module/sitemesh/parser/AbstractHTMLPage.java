/*
 * Title:        AbstractHTMLPage
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.HTMLPage;

import java.io.IOException;
import java.io.Writer;

/**
 * Abstract implementation of {@link com.opensymphony.module.sitemesh.HTMLPage}.
 *
 * <p>Adds to {@link com.opensymphony.module.sitemesh.parser.AbstractPage} some HTML methods.
 * To implement, follow guidelines of super-class, and implement the 2
 * abstract methods states below.</p>
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.5 $
 *
 * @see com.opensymphony.module.sitemesh.parser.AbstractPage
 * @see com.opensymphony.module.sitemesh.HTMLPage
 */
public abstract class AbstractHTMLPage extends AbstractPage implements HTMLPage {

    /**
     * Write data of html <code>&lt;head&gt;</code> tag.
     *
     * <p>Must be implemented. Data written should not actually contain the
     * head tags, but all the data in between.
     */
    public abstract void writeHead(Writer out) throws IOException;

    public boolean isFrameSet() {
        return isPropertySet("frameset") && getProperty("frameset").equalsIgnoreCase("true");
    }

    public void setFrameSet(boolean frameset) {
        addProperty("frameset", frameset ? "true" : "false");
    }
}