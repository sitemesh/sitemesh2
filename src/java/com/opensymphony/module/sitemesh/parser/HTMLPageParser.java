package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.html.HTMLProcessor;
import com.opensymphony.module.sitemesh.html.State;
import com.opensymphony.module.sitemesh.html.StateTransitionRule;
import com.opensymphony.module.sitemesh.html.tokenizer.TagTokenizer;
import com.opensymphony.module.sitemesh.html.util.CharArray;
import com.opensymphony.module.sitemesh.html.rules.BodyTagRule;
import com.opensymphony.module.sitemesh.html.rules.ContentBlockExtractingRule;
import com.opensymphony.module.sitemesh.html.rules.FramesetRule;
import com.opensymphony.module.sitemesh.html.rules.HeadExtractingRule;
import com.opensymphony.module.sitemesh.html.rules.HtmlAttributesRule;
import com.opensymphony.module.sitemesh.html.rules.MSOfficeDocumentPropertiesRule;
import com.opensymphony.module.sitemesh.html.rules.MetaTagRule;
import com.opensymphony.module.sitemesh.html.rules.ParameterExtractingRule;
import com.opensymphony.module.sitemesh.html.rules.TitleExtractingRule;
import com.opensymphony.module.sitemesh.html.rules.PageBuilder;

import java.io.IOException;

/**
 * <p>Builds an HTMLPage object from an HTML document. This behaves
 * similarly to the FastPageParser, however it's a complete rewrite that is simpler to add custom features to such as
 * extraction and transformation of elements.</p>
 *
 * <p>To customize the rules used, this class can be extended and have the userDefinedRules() methods overridden.</p>
 *
 * @author Joe Walnes
 *
 * @see HTMLProcessor
 */
public class HTMLPageParser implements PageParser {

    public Page parse(char[] data) throws IOException {
        CharArray head = new CharArray(64);
        CharArray body = new CharArray(4096);
        TokenizedHTMLPage page = new TokenizedHTMLPage(data, body, head);
        HTMLProcessor processor = new HTMLProcessor(data, body);
        State html = processor.defaultState();

        // Core rules for SiteMesh to be functional.
        html.addRule(new HeadExtractingRule(head)); // contents of <head>
        html.addRule(new BodyTagRule(page, body)); // contents of <body>
        html.addRule(new TitleExtractingRule(page)); // the <title>
        html.addRule(new FramesetRule(page)); // if the page is a frameset

        // Additional rules - designed to be tweaked.
        addUserDefinedRules(html, page);

        processor.process();
        return page;
    }

    protected void addUserDefinedRules(State html, PageBuilder page) {
        // Ensure that while in <xml> tag, none of the other rules kick in.
        // For example <xml><book><title>hello</title></book></xml> should not change the affect the title of the page.
        State xml = new State();
        html.addRule(new StateTransitionRule("xml", xml));

        // Useful properties
        html.addRule(new HtmlAttributesRule(page));         // attributes in <html> element
        html.addRule(new MetaTagRule(page));                // all <meta> tags
        html.addRule(new ParameterExtractingRule(page));    // <parameter> blocks
        html.addRule(new ContentBlockExtractingRule(page)); // <content> blocks

        // Capture properties written to documents by MS Office (author, version, company, etc).
        // Note: These properties are from the xml state, not the html state.
        xml.addRule(new MSOfficeDocumentPropertiesRule(page));
    }

}
