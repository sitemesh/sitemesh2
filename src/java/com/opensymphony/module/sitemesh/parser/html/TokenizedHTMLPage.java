package com.opensymphony.module.sitemesh.parser.html;

import com.opensymphony.module.sitemesh.parser.AbstractHTMLPage;
import com.opensymphony.module.sitemesh.util.CharArray;

import java.io.IOException;
import java.io.Writer;

/**
 * HTMLPage implementation that builds itself based on the incoming 'tag' and 'text' tokens fed to it from the
 * HTMLTagTokenizer.
 *
 * @see HTMLPageParser
 * @see HTMLTagTokenizer
 *
 * @author Joe Walnes
 */
public class TokenizedHTMLPage extends AbstractHTMLPage implements TokenHandler {

    private final CharArray head = new CharArray(512);
    private final CharArray body = new CharArray(4096);

    private boolean inTitle;
    private boolean inHead;
    private String contentBlockId;
    private boolean titleWritten;
    private boolean bodyWritten;

    public TokenizedHTMLPage(char[] original) {
        this.pageData = original;
        addProperty("title", "");
    }


    // ****** Methods to process incoming tags and text (required to implement TokenHandler) ******

    public boolean caresAboutTag(String name) {
        name = name.toLowerCase();
        return name.equals("title")
                || name.equals("html")
                || name.equals("head")
                || name.equals("body")
                || name.equals("meta")
                || name.equals("content")
                || name.equals("parameter");
    }

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
                addProperty("meta." + tag.getAttributeValue("name"), tag.getAttributeValue("content"));
            } else if (tag.hasAttribute("http-equiv")) {
                addProperty("meta.http-equiv." + tag.getAttributeValue("http-equiv"), tag.getAttributeValue("content"));
            }
        } else if (name.equals("body")) {
            if (tag.getType() == Tag.OPEN || tag.getType() == Tag.EMPTY) {
                for (int i = 0; i < tag.getAttributeCount(); i++) {
                    addProperty("body." + tag.getAttributeName(i), tag.getAttributeValue(i));
                }
                body.clear();
            } else {
                bodyWritten = true;
            }
        } else if (name.equals("html")) {
            for (int i = 0; i < tag.getAttributeCount(); i++) {
                addProperty(tag.getAttributeName(i), tag.getAttributeValue(i));
            }
        } else if (name.equals("parameter")) {
            addProperty("page." + tag.getAttributeValue("name"), tag.getAttributeValue("value"));
        }

        if (inHead && !name.equals("head") && !name.equals("title")) {
            tag.writeTo(head);
        }
        if (!inHead && !bodyWritten && !name.equals("body") && !name.equals("html")
                && !name.equals("head") && !name.equals("title") && !name.equals("parameter") && !name.equals("content")) {
            tag.writeTo(body);
        }
    }

    public void text(Text text) {
        if (inTitle && !titleWritten) {
            addProperty("title", text.getText());
            titleWritten = true;
        } else if (contentBlockId != null) {
            addProperty("page." + contentBlockId, text.getText());
        }

        if (inHead && !inTitle) {
            text.writeTo(head);
        }
        if (!inHead && !inTitle && !bodyWritten && contentBlockId == null) {
            text.writeTo(body);
        }
    }

    public void error(String message, int line, int column) {
        // TODO
//        System.out.println(line + "," + column + ": " + message);
    }


    // ****** Methods to access captured page data (required to implement HTMLPage) ******

    public void writeHead(Writer out) throws IOException {
        out.write(head.toString());
    }

    public void writeBody(Writer out) throws IOException {
        out.write(body.toString());
    }

    public String getHead() {
        return head.toString();
    }

    public boolean isFrameSet() {
        // TODO
        return false;
    }

    public String getPage() {
        return new String(pageData);
    }

    public String getBody() {
        return body.toString();
    }

}
