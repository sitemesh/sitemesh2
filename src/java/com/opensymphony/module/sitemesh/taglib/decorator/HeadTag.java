/*
 * Title:        HeadTag
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.taglib.decorator;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.taglib.AbstractTag;

import javax.servlet.jsp.JspException;

/**
 * Write original HTMLPage head to out.
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.1 $
 *
 * @see com.opensymphony.module.sitemesh.HTMLPage#writeHead(java.io.Writer)
 */
public class HeadTag extends AbstractTag {
    public final int doEndTag() throws JspException {
        try {
            HTMLPage htmlPage = (HTMLPage)getPage();
            htmlPage.writeHead(pageContext.getOut());
        }
        catch (Exception e) {
            trace(e);
        }
        return EVAL_PAGE;
    }
}