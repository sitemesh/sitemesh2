package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.html.HTMLProcessor;
import com.opensymphony.module.sitemesh.parser.rules.BodyTagRule;
import com.opensymphony.module.sitemesh.parser.rules.ContentBlockExtractingRule;
import com.opensymphony.module.sitemesh.parser.rules.FramesetRule;
import com.opensymphony.module.sitemesh.parser.rules.HeadExtractingRule;
import com.opensymphony.module.sitemesh.parser.rules.HtmlAttributesRule;
import com.opensymphony.module.sitemesh.parser.rules.MetaTagRule;
import com.opensymphony.module.sitemesh.parser.rules.ParameterExtractingRule;
import com.opensymphony.module.sitemesh.parser.rules.TitleExtractingRule;
import com.opensymphony.module.sitemesh.html.tokenizer.TagTokenizer;
import com.opensymphony.module.sitemesh.html.util.BufferStack;
import com.opensymphony.module.sitemesh.html.util.CharArray;

import java.io.IOException;

/**
 * <b>WARNING - This is experimental - use at own risk!</b> Builds an HTMLPage object from an HTML document. This behaves
 * similarly to the FastPageParser, however it's a complete rewrite that is simpler to add custom features to such as
 * extraction and transformation of elements.
 *
 * @see TagTokenizer
 *
 * @author Joe Walnes
 */
public class HTMLPageParser implements PageParser {

    public Page parse(char[] data) throws IOException {
        CharArray head = new CharArray(64);
        CharArray body = new CharArray(4096);

        HTMLPage page = new TokenizedHTMLPage(data, body, head);

        BufferStack bufferStack = new BufferStack();
        bufferStack.pushBuffer(body);

        HTMLProcessor htmlProcessor = new HTMLProcessor(data, bufferStack);
        htmlProcessor.addRule("html", new HtmlAttributesRule(page));
        htmlProcessor.addRule("head", new HeadExtractingRule(head));
        htmlProcessor.addRule("meta", new MetaTagRule(page));
        htmlProcessor.addRule("title", new TitleExtractingRule(page));
        htmlProcessor.addRule("body", new BodyTagRule(page, body));
        htmlProcessor.addRule("parameter", new ParameterExtractingRule(page));
        htmlProcessor.addRule("content", new ContentBlockExtractingRule(page));
        htmlProcessor.addRule("frame", new FramesetRule(page));
        htmlProcessor.addRule("frameset", new FramesetRule(page));

        htmlProcessor.process();

        return page;
    }

}
