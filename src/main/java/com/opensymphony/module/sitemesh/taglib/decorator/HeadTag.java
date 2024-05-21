/*
 * Title:        HeadTag
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.taglib.decorator;

import java.io.IOException;

import jakarta.servlet.jsp.JspException;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.taglib.AbstractTag;

/**
 * Write original HTMLPage head to out.
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.3 $
 *
 * @see com.opensymphony.module.sitemesh.HTMLPage#writeHead(java.io.Writer)
 */
public class HeadTag extends AbstractTag {
    public final int doEndTag() throws JspException
    {
      HTMLPage htmlPage = (HTMLPage)getPage();
      try
      {
        htmlPage.writeHead(getOut());
      }
      catch(IOException e)
      {
        throw new JspException("Error writing head element: " + e.toString(), e);
      }
      return EVAL_PAGE;
    }
}