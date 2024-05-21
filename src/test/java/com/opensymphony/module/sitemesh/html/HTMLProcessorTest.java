package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.html.rules.TagReplaceRule;
import com.opensymphony.module.sitemesh.html.util.StringSitemeshBuffer;

import junit.framework.TestCase;

import java.io.IOException;

public class HTMLProcessorTest extends TestCase {

    private SitemeshBufferFragment.Builder body;

    private HTMLProcessor createProcessor(String input) {
        SitemeshBuffer buffer = new StringSitemeshBuffer(input);
        body = SitemeshBufferFragment.builder().setBuffer(buffer);
        return new HTMLProcessor(buffer, body);
    }

    public void testCreatesStateTransitionEvent() throws IOException {
		String input = "<a></a>";
		HTMLProcessor htmlProcessor = createProcessor(input);

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
        HTMLProcessor processor = createProcessor("<hello><b id=\"something\">world</b></hello>");
        processor.addRule(new TagReplaceRule("b", "strong"));

        processor.process();
        assertEquals("<hello><strong id=\"something\">world</strong></hello>", body.build().getStringContent());
    }

    public void testAllowsRulesToModifyAttributes() throws IOException {
        HTMLProcessor processor = createProcessor("<hello><a href=\"modify-me\">world</a></hello>");
        processor.addRule(new BasicRule("a") {
            public void process(Tag tag) {
                currentBuffer().delete(tag.getPosition(), tag.getLength());
                CustomTag customTag = new CustomTag(tag);
                String href = customTag.getAttributeValue("href", false);
                if (href != null) {
                    href = href.toUpperCase();
                    customTag.setAttributeValue("href", true, href);
                }
                customTag.writeTo(currentBuffer(), tag.getPosition());
            }
        });

        processor.process();
        assertEquals("<hello><a href=\"MODIFY-ME\">world</a></hello>", body.build().getStringContent());
    }

    public void testSupportsChainedFilteringOfTextContent() throws IOException {
        HTMLProcessor processor = createProcessor("<hello>world</hello>");
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
        assertEquals("<HELLo>WoRLD</HELLo>", body.build().getStringContent());
    }

    public void testSupportsTextFiltersForSpecificStates() throws IOException {
        HTMLProcessor processor = createProcessor("la la<br> la la <capitalism>laaaa<br> laaaa</capitalism> la la");
        State capsState = new State();
        processor.addRule(new StateTransitionRule("capitalism", capsState, true));

        capsState.addTextFilter(new TextFilter() {
            public String filter(String text) {
                return text.toUpperCase();
            }
        });

        processor.process();
        assertEquals("la la<br> la la <capitalism>LAAAA<BR> LAAAA</capitalism> la la", body.build().getStringContent());
    }

    public void testCanAddAttributesToCustomTag() throws IOException {
        String html = "<h1>Headline</h1>";
        HTMLProcessor htmlProcessor = createProcessor(html);
        htmlProcessor.addRule(new BasicRule() {
            public boolean shouldProcess(String tag) {
                return tag.equalsIgnoreCase("h1");
            }

            public void process(Tag tag) {
                if (tag.getType() == Tag.OPEN) {
                    currentBuffer().delete(tag.getPosition(), tag.getLength());
                    CustomTag ctag = new CustomTag(tag);
                    ctag.addAttribute("class", "y");
                    assertEquals(1, ctag.getAttributeCount());
                    ctag.writeTo(currentBuffer(), tag.getPosition());
                }
            }
        });
        htmlProcessor.process();
        assertEquals("<h1 class=\"y\">Headline</h1>", body.build().getStringContent());
    }
}
