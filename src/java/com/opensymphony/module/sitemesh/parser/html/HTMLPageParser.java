package com.opensymphony.module.sitemesh.parser.html;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;

import java.io.IOException;

/**
 * Builds an HTMLPage object from an HTML document.
 *
 * This is backed by the HTMLTagTokenizer which tokenizes an HTML page into a stream of tags and text.
 *
 * @author Joe Walnes
 */
public class HTMLPageParser implements PageParser {

    public Page parse(char[] data) throws IOException {
        final HTMLPage result = new HTMLPage(data);
        result.addProperty("title", "");
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer(data);
        tokenizer.start(new TokenHandler() {

            private boolean inTitle;
            private boolean inHead;
            private String contentBlockId;
            private boolean titleWritten;
            private boolean bodyWritten;

            public void tag(Tag tag) {
                String name = tag.getName().toLowerCase();
                if (name.equals("title")) {
                    inTitle = tag.getType() == Tag.OPEN;
                } else if (name.equals("head")) {
                    inHead = tag.getType() == Tag.OPEN;
                } else if (name.equals("content")) {
                    if (tag.getType() == Tag.OPEN) {
                        contentBlockId = tag.getAttributeValue("tag");
                    } else {
                        contentBlockId = null;
                    }
                } else if (name.equals("meta")) {
                    if (tag.hasAttribute("name")) {
                        result.addProperty("meta." + tag.getAttributeValue("name"), tag.getAttributeValue("content"));
                    } else if (tag.hasAttribute("http-equiv")) {
                        result.addProperty("meta.http-equiv." + tag.getAttributeValue("http-equiv"), tag.getAttributeValue("content"));
                    }
                } else if (name.equals("body")) {
                    if (tag.getType() == Tag.OPEN || tag.getType() == Tag.EMPTY) {
                        for (int i = 0; i < tag.getAttributeCount(); i++) {
                            result.addProperty("body." + tag.getAttributeName(i), tag.getAttributeValue(i));
                        }
                        result.clearBody();
                    } else {
                        bodyWritten = true;
                    }
                } else if (name.equals("html")) {
                    for (int i = 0; i < tag.getAttributeCount(); i++) {
                        result.addProperty(tag.getAttributeName(i), tag.getAttributeValue(i));
                    }
                } else if (name.equals("parameter")) {
                    result.addProperty("page." + tag.getAttributeValue("name"), tag.getAttributeValue("value"));
                } else {
                    //result.appendToBody(tag.getCompleteTag());
                }

                if (inHead && !name.equals("head") && !name.equals("title")) {
                    result.appendToHead(tag.getCompleteTag());
                }
                if (!inHead && !bodyWritten && !name.equals("body") && !name.equals("html")
                        && !name.equals("head") && !name.equals("title") && !name.equals("parameter") && !name.equals("content")) {
                    result.appendToBody(tag.getCompleteTag());
                }
            }

            public void text(Text text) {
                if (inTitle && !titleWritten) {
                    result.addProperty("title", text.getText());
                    titleWritten = true;
                } else if (contentBlockId != null) {
                    result.addProperty("page." + contentBlockId, text.getText());
                } else {
                   //result.appendToBody(text.getText());
                }

                if (inHead && !inTitle) {
                    result.appendToHead(text.getText());
                }
                if (!inHead && !inTitle && !bodyWritten && contentBlockId == null) {
                    result.appendToBody(text.getText());
                }
            }

            public void error(String message, int line, int column) {
                //fail("Encountered error");
            }
        });
        return result;
    }


}
