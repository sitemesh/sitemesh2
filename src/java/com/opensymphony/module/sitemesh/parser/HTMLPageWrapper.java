/*
 * Title:        HTMLPageWrapper
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
 * Wrapper class that relays all methods to an original Page.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.1 $
 *
 * @see PageWrapper
 */
public final class HTMLPageWrapper extends PageWrapper implements HTMLPage {
	/** Create wrapper using supplied HTMLPage as original. */
	public HTMLPageWrapper(HTMLPage original) {
		super(original);
	}

	/** Return original HTMLPage. */
	protected HTMLPage hpage() {
		return (HTMLPage)page();
	}

	public void writeHead(Writer out) throws IOException {
		hpage().writeHead(out);
	}

	public boolean isFrameSet() {
		return hpage().isFrameSet();
	}
}