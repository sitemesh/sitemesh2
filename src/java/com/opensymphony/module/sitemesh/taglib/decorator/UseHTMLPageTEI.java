/*
 * Title:        UseHTMLPageTEI
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.taglib.decorator;

/**
 * TagExtraInfo implementation to expose HTMLPage object as variable.
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.1 $
 *
 * @see UsePageTag
 * @see UsePageTEI
 */
public class UseHTMLPageTEI extends UsePageTEI {
    protected String getType() {
        return "com.opensymphony.module.sitemesh.HTMLPage";
    }
}
