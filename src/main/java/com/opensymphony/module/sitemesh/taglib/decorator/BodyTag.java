/*
 * Title:        BodyTag
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.taglib.decorator;

import com.opensymphony.module.sitemesh.taglib.AbstractTag;

/**
 * Write original Page body to out.
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @author <a href="scott@atlassian.com">Scott Farquhar</a>
 * @version $Revision: 1.2 $
 *
 * @see com.opensymphony.module.sitemesh.HTMLPage#writeBody(java.io.Writer)
 */
public class BodyTag extends AbstractTag {
    public final int doEndTag() {
        try {
            getPage().writeBody(getOut());
        }
        catch (Exception e) {
            trace(e);
        }
        return EVAL_PAGE;
    }

}