package com.opensymphony.module.sitemesh.multipass;

import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.multipass.DivExtractingPageParser;
import junit.framework.TestCase;

import java.io.IOException;

/**
 * @author Joe Walnes
 */
public class DivExtractingPageParserTest extends TestCase {

    public void testReplacesTopLevelDivsWithPlaceHolders() throws IOException {
        String input = "" +
                "<html>\n" +
                "  <head><title>Title</title></head>\n" +
                "  <body>\n" +
                "    <div id='one'>Hello</div>\n" +
                "    Blah\n" +
                "    <div id='two'>World<br><div id=inner>Great</div></div>\n" +
                "    <div>Bye</div>\n" +
                "  </body>\n" +
                "</html>";

        PageParser parser = new DivExtractingPageParser();
        Page page = parser.parse(new DefaultSitemeshBuffer(input.toCharArray()));

        String expectedBody = "" +
                "    <sitemesh:multipass id=\"div.one\"/>\n" +
                "    Blah\n" +
                "    <sitemesh:multipass id=\"div.two\"/>\n" +
                "    <div>Bye</div>\n";
        assertEquals("Title", page.getTitle());
        assertEquals(expectedBody.trim(), page.getBody().trim());
        assertEquals("<div id='one'>Hello</div>", page.getProperty("div.one"));
        assertEquals("<div id='two'>World<br><div id=inner>Great</div></div>", page.getProperty("div.two"));
    }

}

