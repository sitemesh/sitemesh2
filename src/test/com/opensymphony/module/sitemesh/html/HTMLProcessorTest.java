package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.html.util.CharArray;
import com.opensymphony.module.sitemesh.html.rules.TagReplaceRule;

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
        Reader in = new StringReader("<hello><b id=\"something\">world</b></hello>");
        Writer out = new StringWriter();

        HTMLProcessor processor = new HTMLProcessor(in, out);
        processor.addRule(new TagReplaceRule("b", "strong"));

        processor.process();
        assertEquals("<hello><strong id=\"something\">world</strong></hello>", out.toString());
    }

    public void testAllowsRulesToModifyAttributes() throws IOException {
        Reader in = new StringReader("<hello><a href=\"modify-me\">world</a></hello>");
        Writer out = new StringWriter();

        HTMLProcessor processor = new HTMLProcessor(in, out);
        processor.addRule(new BasicRule("a") {
            public void process(Tag tag) {
                CustomTag customTag = new CustomTag(tag);
                String href = customTag.getAttributeValue("href", false);
                if (href != null) {
                    href = href.toUpperCase();
                    customTag.setAttributeValue("href", true, href);
                }
                customTag.writeTo(currentBuffer());
            }
        });

        processor.process();
        assertEquals("<hello><a href=\"MODIFY-ME\">world</a></hello>", out.toString());
    }

    public void testSupportsChainedFilteringOfTextContent() throws IOException {
        Reader in = new StringReader("<hello>world</hello>");
        Writer out = new StringWriter();

        HTMLProcessor processor = new HTMLProcessor(in, out);
        processor.addTextFilter(new TextFilter() {
            public String filter(String text) {
                return text.toUpperCase();
            }
        });
        processor.addTextFilter(new TextFilter() {
            public String filter(String text) {
                return text.replaceAll("O", "o");
            }
        });

        processor.process();
        assertEquals("<HELLo>WoRLD</HELLo>", out.toString());
    }

    public void testSupportsTextFiltersForSpecificStates() throws IOException {
        Reader in = new StringReader("la la<br> la la <capitalism>laaaa<br> laaaa</capitalism> la la");
        Writer out = new StringWriter();

        HTMLProcessor processor = new HTMLProcessor(in, out);

        State capsState = new State();
        processor.addRule(new StateTransitionRule("capitalism", capsState, true));

        capsState.addTextFilter(new TextFilter() {
            public String filter(String text) {
                return text.toUpperCase();
            }
        });

        processor.process();
        assertEquals("la la<br> la la <capitalism>LAAAA<BR> LAAAA</capitalism> la la", out.toString());
    }

}
