/*
 * Title:        InlineDecoratorMapper
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.mapper;

import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.module.sitemesh.factory.FactoryException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The InlineDecoratorMapper is used to determine the correct Decorator when
 * using inline decorators.
 *
 * <p>It will check the request attribute value defined by the key
 * {@link com.opensymphony.module.sitemesh.RequestConstants#DECORATOR} and use the appropriate named
 * Decorator. This is passed across from the page:applyDecorator tag.</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.2 $
 *
 * @see com.opensymphony.module.sitemesh.DecoratorMapper
 */
public class InlineDecoratorMapper extends AbstractDecoratorMapper implements RequestConstants {
	public Decorator getDecorator(HttpServletRequest request, Page page) {
		Decorator result = null;
		if (request.getAttribute(DECORATOR) != null) {
			// Retrieve name of decorator to use from request
			String decoratorName = (String)request.getAttribute(DECORATOR);
			result = getNamedDecorator(request, decoratorName);
			if (result == null) throw new FactoryException("Cannot locate inline Decorator: " + decoratorName);
		}
		return result == null ? super.getDecorator(request, page) : result;
	}
}
