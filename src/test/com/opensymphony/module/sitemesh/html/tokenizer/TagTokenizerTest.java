package com.opensymphony.module.sitemesh.html.tokenizer;

import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.Text;
import junit.framework.TestCase;

public class TagTokenizerTest extends TestCase {

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
        TagTokenizer tokenizer = new TagTokenizer("<hello>cruel<world><and>some stuff");
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
        TagTokenizer tokenizer = new TagTokenizer("<open></close><empty/>");
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
        TagTokenizer tokenizer = new TagTokenizer("hello world <!-- how are<we> \n -doing? -->good\n bye.<br>");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testExtractsUnquotedAttributesFromTag() {
        // expectations
        handler.expectTag(Tag.OPEN, "hello", new String[]{"name", "world", "foo", "boo"});
        // execute
        TagTokenizer tokenizer = new TagTokenizer("<hello name=world foo=boo>");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testExtractsQuotedAttributesFromTag() {
        // expectations
        handler.expectTag(Tag.OPEN, "hello", new String[]{"name", "the world", "foo", "boo"});
        // execute
        TagTokenizer tokenizer = new TagTokenizer("<hello name=\"the world\" foo=\"boo\">");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testHandlesMixedQuoteTypesInAttributes() {
        // expectations
        handler.expectTag(Tag.OPEN, "hello", new String[]{"name", "it's good", "foo", "say \"boo\""});
        // execute
        TagTokenizer tokenizer = new TagTokenizer("<hello name=\"it's good\" foo=\'say \"boo\"'>");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testHandlesHtmlStyleEmptyAttributes() {
        // expectations
        handler.expectTag(Tag.OPEN, "hello", new String[]{"isgood", null, "and", null, "stuff", null});
        // execute
        TagTokenizer tokenizer = new TagTokenizer("<hello isgood and stuff>");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testSupportsWhitespaceInElements() {
        // expectations
        handler.expectTag(Tag.OPEN, "hello", new String[]{"somestuff", "good", "foo", null, "x", "long\n string"});
        handler.expectTag(Tag.EMPTY, "empty");
        handler.expectTag(Tag.OPEN, "HTML", new String[]{"notonnewline", "yo", "newline", "hello", "anotherline", "bye"});
        // execute
        TagTokenizer tokenizer = new TagTokenizer(""
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
        TagTokenizer tokenizer = new TagTokenizer("some text" + originalTag + "more text");
        final boolean[] called = {false}; // has to be final array so anonymous inner class can change the value.

        tokenizer.start(new TokenHandler() {

            public boolean shouldProcessTag(String name) {
                return true;
            }

            public void tag(Tag tag) {
                assertEquals(originalTag, tag.getContents());
                called[0] = true;
            }

            public void text(Text text) {
                // ignoring text for this test
            }

            public void warning(String message, int line, int column) {
                fail("Encountered error " + message);
            }
        });

        assertTrue("tag() never called", called[0]);
    }

    public void testAllowsSlashInUnquotedAttribute() {
        // expectations
        handler.expectTag(Tag.OPEN, "something", new String[]{"type", "text/html"});
        // execute
        TagTokenizer tokenizer = new TagTokenizer("<something type=text/html>");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testAllowsTrailingQuoteOnAttribute() {
        // expectations
        handler.expectTag(Tag.OPEN, "something", new String[]{"type", "bl'ah\""});
        // execute
        TagTokenizer tokenizer = new TagTokenizer("<something type=bl'ah\">");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testAllowsAwkwardCharsInElementAndAttribute() {
        // expectations
        handler.expectTag(Tag.OPEN, "name:space", new String[]{"foo:bar", "x:y%"});
        handler.expectTag(Tag.EMPTY, "a_b-c$d", new String[]{"b_b-c$d", "c_b=c$d"});
        handler.expectTag(Tag.OPEN, "a", new String[]{"href", "/exec/obidos/flex-sign-in/ref=pd_nfy_gw_si/026-2634699-7306802?opt=a&page=misc/login/flex-sign-in-secure.html&response=tg/new-for-you/new-for-you/-/main"});
        // execute
        TagTokenizer tokenizer = new TagTokenizer(""
                + "<name:space foo:bar=x:y%>"
                + "<a_b-c$d b_b-c$d=c_b=c$d />"
                + "<a href=/exec/obidos/flex-sign-in/ref=pd_nfy_gw_si/026-2634699-7306802?opt=a&page=misc/login/flex-sign-in-secure.html&response=tg/new-for-you/new-for-you/-/main>");
        tokenizer.start(handler);
        // verify
        handler.verify();

    }

    public void testTreatsXmpCdataScriptAndProcessingInstructionsAsText() {
        // expectations
        handler.expectText("<script language=jscript> if (a < b & > c)\n alert(); </script>");
        handler.expectText("<xmp><evil \n<stuff<</xmp>");
        handler.expectText("<?some stuff ?>");
        handler.expectText("<![CDATA[ evil<>> <\n    ]]>");
        handler.expectText("<SCRIPT>stuff</SCRIPT>");
        handler.expectText("<!DOCTYPE html PUBLIC \\\"-//W3C//DTD HTML 4.01 Transitional//EN\\\">");
        // execute
        TagTokenizer tokenizer = new TagTokenizer(""
                + "<script language=jscript> if (a < b & > c)\n alert(); </script>"
                + "<xmp><evil \n<stuff<</xmp>"
                + "<?some stuff ?>"
                + "<![CDATA[ evil<>> <\n    ]]>"
                + "<SCRIPT>stuff</SCRIPT>"
                + "<!DOCTYPE html PUBLIC \\\"-//W3C//DTD HTML 4.01 Transitional//EN\\\">");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    /* TODO
    public void testTreatsUnterminatedTagAtEofAsText() {
        // expectations
        handler.expectText("hello");
        handler.expectText("<world");
        // execute
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer("hello<world");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testTreatsUnterminatedAttributeNameAtEofAsText() {
        // expectations
        handler.expectText("hello");
        handler.expectText("<world x");
        // execute
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer("hello<world x");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testTreatsUnterminatedQuotedAttributeValueAtEofAsText() {
        // expectations
        handler.expectText("hello");
        handler.expectText("<world x=\"fff");
        // execute
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer("hello<world x=\"fff");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testTreatsUnterminatedUnquotedAttributeValueAtEofAsText() {
        // expectations
        handler.expectText("hello");
        handler.expectText("<world x=fff");
        // execute
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer("hello<world x=fff");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }
    */

    public void testIgnoresEvilMalformedPairOfAngleBrackets() {
        // expectations
        handler.expectTag(Tag.OPEN, "good");
        // execute
        TagTokenizer tokenizer = new TagTokenizer("<>< ><good><>");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testDoesNotTryToParseTagsUnlessTheHandlerCares() {
        // setup
        handler = new MockTokenHandler() {
            public boolean shouldProcessTag(String name) {
                return name.equals("good");
            }
        };
        // expectations
        handler.expectTag(Tag.OPEN, "good");
        handler.expectText("<bad>");
        handler.expectTag(Tag.CLOSE, "good");
        handler.expectText("<![bad]-->");
        // execute
        TagTokenizer tokenizer = new TagTokenizer("<good><bad></good><![bad]-->");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }

    public void testParsesMagicCommentBlocks() {
        // expectations
        handler.expectTag(Tag.OPEN_MAGIC_COMMENT, "if", new String[] {"gte", null, "mso", null, "9", null});
        handler.expectTag(Tag.OPEN, "stuff");
        handler.expectTag(Tag.CLOSE_MAGIC_COMMENT, "endif");
        // execute
        TagTokenizer tokenizer = new TagTokenizer("<!--[if gte mso 9]><stuff><![endif]-->");
        tokenizer.start(handler);
        // verify
        handler.verify();

    }

    public void testToleratesExtraQuoteClosingAttributeValue() {
        // expectations
        handler = new MockTokenHandler() {
            public void warning(String message, int line, int column) {
                // warning ok!
            }
        };
        handler.expectTag(Tag.OPEN, "a", new String[] {"href", "something-with-a-naughty-quote"});
        // execute
        TagTokenizer tokenizer = new TagTokenizer("<a href=\"something-with-a-naughty-quote\"\">");
        tokenizer.start(handler);
        // verify
        handler.verify();
    }
}

