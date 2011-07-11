package com.opensymphony.module.sitemesh.html.rules;

import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import junit.framework.TestCase;

import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;

import com.opensymphony.module.sitemesh.html.HTMLProcessor;

public class RegexReplacementTextFilterTest extends TestCase {

    private SitemeshBufferFragment.Builder body;

    private HTMLProcessor createProcessor(String input) {
        SitemeshBuffer buffer = new DefaultSitemeshBuffer(input.toCharArray());
        body = SitemeshBufferFragment.builder().setBuffer(buffer);
        return new HTMLProcessor(buffer, body);
    }

    public void testReplacesTextContentMatchedByRegularExpression() throws IOException {
        HTMLProcessor processor = createProcessor("<hello>Today is DATE so hi</hello>");
        processor.addTextFilter(new RegexReplacementTextFilter("DATE", "1-jan-2009"));

        processor.process();
        assertEquals("<hello>Today is 1-jan-2009 so hi</hello>", body.build().getStringContent());
    }

    public void testAllowsMatchedGroupToBeUsedInSubsitution() throws IOException {
        HTMLProcessor processor = createProcessor("<hello>I think JIRA:SIM-1234 is the way forward</hello>");
        processor.addTextFilter(new RegexReplacementTextFilter(
                "JIRA:([A-Z]+\\-[0-9]+)",
                "<a href='http://jira.opensymhony.com/browse/$1'>$1</a>"));

        processor.process();
        assertEquals(
                "<hello>I think <a href='http://jira.opensymhony.com/browse/SIM-1234'>SIM-1234</a> is the way forward</hello>",
                body.build().getStringContent());
    }

}
