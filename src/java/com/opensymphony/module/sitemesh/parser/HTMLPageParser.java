package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.html.HTMLProcessor;
import com.opensymphony.module.sitemesh.html.State;
import com.opensymphony.module.sitemesh.html.StateTransitionRule;
import com.opensymphony.module.sitemesh.html.tokenizer.TagTokenizer;
import com.opensymphony.module.sitemesh.html.util.CharArray;
import com.opensymphony.module.sitemesh.parser.rules.BodyTagRule;
import com.opensymphony.module.sitemesh.parser.rules.ContentBlockExtractingRule;
import com.opensymphony.module.sitemesh.parser.rules.FramesetRule;
import com.opensymphony.module.sitemesh.parser.rules.HeadExtractingRule;
import com.opensymphony.module.sitemesh.parser.rules.HtmlAttributesRule;
import com.opensymphony.module.sitemesh.parser.rules.MetaTagRule;
import com.opensymphony.module.sitemesh.parser.rules.ParameterExtractingRule;
import com.opensymphony.module.sitemesh.parser.rules.TitleExtractingRule;

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

        HTMLProcessor htmlProcessor = new HTMLProcessor(data, body);

        State defaultState = htmlProcessor.defaultState();
        State xmlState = new State();

        defaultState.addRule("html", new HtmlAttributesRule(page));
        defaultState.addRule("head", new HeadExtractingRule(head));
        defaultState.addRule("meta", new MetaTagRule(page));
        defaultState.addRule("title", new TitleExtractingRule(page));
        defaultState.addRule("body", new BodyTagRule(page, body));
        defaultState.addRule("parameter", new ParameterExtractingRule(page));
        defaultState.addRule("content", new ContentBlockExtractingRule(page));
        defaultState.addRule("frame", new FramesetRule(page));
        defaultState.addRule("frameset", new FramesetRule(page));
        defaultState.addRule("xml", new StateTransitionRule(xmlState, true));


        // extended rules

        htmlProcessor.process();

        return page;
    }

}
