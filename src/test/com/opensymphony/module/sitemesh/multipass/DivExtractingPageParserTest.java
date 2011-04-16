package com.opensymphony.module.sitemesh.multipass;

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
        Page page = parser.parse(input.toCharArray());

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

    public void testExtractAttributes() throws IOException {
        String input = "" +
                "<html>\n" +
                "  <head><title>Title</title></head>\n" +
                "  <body>\n" +
                "    <div id='one' class='c_one' align='center'>Hello</div>\n" +
                "    Blah\n" +
                "    <div id='two'>World<br><div id=inner>Great</div></div>\n" +
                "    <div>Bye</div>\n" +
                "  </body>\n" +
                "</html>";

        PageParser parser = new DivExtractingPageParser();
        Page page = parser.parse(input.toCharArray());

        assertEquals("c_one", page.getProperty("div.one.class"));
        assertEquals("center", page.getProperty("div.one.align"));
        assertEquals("two", page.getProperty("div.two.id"));
    }
}

