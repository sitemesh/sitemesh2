package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.html.util.CharArray;
import com.opensymphony.module.sitemesh.parser.rules.TagReplaceRule;

import junit.framework.TestCase;

import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;

public class HTMLProcessorTest extends TestCase {

	public void testCreatesStateTransitionEvent() throws IOException {
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

    public void testSupportsConventionalReaderAndWriter() throws IOException {
        Reader in = new StringReader("<hello><b>world</b></hello>");
        Writer out = new StringWriter();

        HTMLProcessor processor = new HTMLProcessor(in, out);
        processor.defaultState().addRule(new TagReplaceRule("b", "strong"));

        processor.process();
        assertEquals("<hello><strong>world</strong></hello>", out.toString());
    }
}
