package com.opensymphony.module.sitemesh.parser.html;

import junit.framework.TestCase;

import java.io.StringReader;

public class HTMLTagTokenizerTest extends TestCase {

    private MockTokenHandler handler;

    protected void setUp() throws Exception {
        super.setUp();
        handler = new MockTokenHandler();
    }

    public void testSplitsTagsFromText() {
        // expectations
        handler.expectTag(Tag.OPEN, "hello");
        handler.expectText("cruel");
        handler.expectTag(Tag.OPEN, "world");
        handler.expectTag(Tag.OPEN, "and");
        handler.expectText("some stuff");
        // execute
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer("<hello>cruel<world><and>some stuff");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testDistinguishesBetweenOpenCloseAndEmptyTags() {
        // expectations
        handler.expectTag(Tag.OPEN, "open");
        handler.expectTag(Tag.CLOSE, "close");
        handler.expectTag(Tag.EMPTY, "empty");
        // execute
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer("<open></close><empty/>");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testTreatsCommentsAsText() {
        // expectations
        handler.expectText("hello world ");
        handler.expectText("<!-- how are<we> \n -doing? -->");
        handler.expectText("good\n bye.");
        handler.expectTag(Tag.OPEN, "br");
        // execute
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer("hello world <!-- how are<we> \n -doing? -->good\n bye.<br>");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testExtractsUnquotedAttributesFromTag() {
        // expectations
        handler.expectTag(Tag.OPEN, "hello", new String[] {"name", "world", "foo", "boo"});
        // execute
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer("<hello name=world foo=boo>");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testExtractsQuotedAttributesFromTag() {
        // expectations
        handler.expectTag(Tag.OPEN, "hello", new String[] {"name", "the world", "foo", "boo"});
        // execute
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer("<hello name=\"the world\" foo=\"boo\">");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testHandlesMixedQuoteTypesInAttributes() {
        // expectations
        handler.expectTag(Tag.OPEN, "hello", new String[] {"name", "it's good", "foo", "say \"boo\""});
        // execute
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer("<hello name=\"it's good\" foo=\'say \"boo\"'>");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testHandlesHtmlStyleEmptyAttributes() {
        // expectations
        handler.expectTag(Tag.OPEN, "hello", new String[] {"isgood", null, "and", null, "stuff", null});
        // execute
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer("<hello isgood and stuff>");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testSupportsWhitespaceInElements() {
        // expectations
        handler.expectTag(Tag.OPEN, "hello", new String[] {"somestuff", "good", "foo", null, "x", "long\n string"});
        handler.expectTag(Tag.EMPTY, "empty");
        handler.expectTag(Tag.OPEN, "HTML", new String[] {"notonnewline", "yo", "newline", "hello", "anotherline", "bye"});
        // execute
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer(""
                + "<hello \n somestuff = \ngood \n   foo \nx=\"long\n string\"   >"
                + "<empty      />"
                + "<HTML notonnewline=yo newline=\n"
                + "hello anotherline=\n"
                + "\"bye\">");
        tokenizer.start(handler);
        // verify
        handler.verify();


    }

    public void testExposesOriginalTagToHandler() {
        // Should really use a mock library for this expectation, but I'd rather not
        // add a new dependency for the sake of a single test.
        final String originalTag = "<hello \n somestuff = \ngood \n   foo \nx=\"long\n string\"   >";
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer("some text" + originalTag + "more text");
        final boolean[] called = {false}; // has to be final array so anonymous inner class can change the value.

        tokenizer.start(new TokenHandler() {
            public void tag(Tag tag) {
                assertEquals(originalTag, tag.getCompleteTag());
                called[0] = true;
            }

            public void text(Text text) {
                // ignoring text for this test
            }

            public void error(String message, int line, int column) {
                fail("Encountered error " + message);
            }
        });

        assertTrue("tag() never called", called[0]);
    }

    public void testAllowsSlashInUnquotedAttribute() {
        // expectations
        handler.expectTag(Tag.OPEN, "something", new String[] { "type", "text/html" });
        // execute
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer("<something type=text/html>");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testAllowsTrailingQuoteOnAttribute() {
        // expectations
        handler.expectTag(Tag.OPEN, "something", new String[] { "type", "bl'ah\"" });
        // execute
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer("<something type=bl'ah\">");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testAllowsAwkwardCharsInElementAndAttribute() {
        // expectations
        handler.expectTag(Tag.OPEN, "name:space", new String[] { "foo:bar", "x:y%" });
        handler.expectTag(Tag.EMPTY, "a_b-c$d", new String[] { "b_b-c$d", "c_b=c$d" });
        // execute
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer(""
                + "<name:space foo:bar=x:y%>"
                + "<a_b-c$d b_b-c$d=c_b=c$d />");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testTreatsXmlXmpCdataScriptAndProcessingInstructionsAsText() {
        // expectations
        handler.expectText("<script language=jscript> if (a < b & > c)\n alert(); </script>");
        handler.expectText("<xml><evil \n<stuff<</xml>");
        handler.expectText("<xmp><evil \n<stuff<</xmp>");
        handler.expectText("<?some stuff ?>");
        handler.expectText("<![CDATA[ evil<>> <\n    ]]>");
        handler.expectText("<SCRIPT>stuff</SCRIPT>");
        handler.expectText("<!DOCTYPE html PUBLIC \\\"-//W3C//DTD HTML 4.01 Transitional//EN\\\">");
        // execute
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer(""
                + "<script language=jscript> if (a < b & > c)\n alert(); </script>"
                + "<xml><evil \n<stuff<</xml>"
                + "<xmp><evil \n<stuff<</xmp>"
                + "<?some stuff ?>"
                + "<![CDATA[ evil<>> <\n    ]]>"
                + "<SCRIPT>stuff</SCRIPT>"
                + "<!DOCTYPE html PUBLIC \\\"-//W3C//DTD HTML 4.01 Transitional//EN\\\">");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testIgnoresEvilMalformedPairOfAngleBrackets() {
        // expectations
        handler.expectTag(Tag.OPEN, "good");
        // execute
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer("<>< ><good><>");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

}

