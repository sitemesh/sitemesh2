/*
 * Title:        DivTag
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
 * Write a HTMLPage div to out.
 *
 * @author mouyang
 * @version $Revision: 1.3 $
 *
 * @see com.opensymphony.module.sitemesh.HTMLPage#writeHead(java.io.Writer)
 */
public class DivTag extends AbstractTag {
    protected String divId;

    public String getId() {
        return divId;
    }

    public void setId(String divId) {
        this.divId = divId;
    }
    public final int doEndTag() throws JspException
    {
      try
      {
        String divBody = getPage().getProperty("div." + divId);
        if (divBody != null) {
          getOut().write(divBody.substring(divBody.indexOf('>') + 1, divBody.lastIndexOf('<')));
        }
      }
      catch(IOException e)
      {
        throw new JspException("Error writing head element: " + e.toString(), e);
      }
      return EVAL_PAGE;
    }
}