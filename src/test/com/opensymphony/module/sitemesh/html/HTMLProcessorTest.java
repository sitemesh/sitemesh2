package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.html.util.CharArray;

import junit.framework.TestCase;

public class HTMLProcessorTest extends TestCase {

	public void testCreatesStateTransitionEvent() {		
		char[] input = "<a></a>".toCharArray();
		HTMLProcessor htmlProcessor = new HTMLProcessor(input, new CharArray(128));
		
		State defaultState = htmlProcessor.defaultState();
		
		final StringBuffer stateLog = new StringBuffer();
		
		defaultState.addListener(new StateChangeListener() {
			public void stateFinished() {
				stateLog.append("finished");
			}
		});
		
		htmlProcessor.process();
		assertEquals("finished", stateLog.toString());
	}
}
