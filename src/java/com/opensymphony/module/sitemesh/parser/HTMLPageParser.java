package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.parser.tokenizer.HTMLTagTokenizer;

import java.io.IOException;

/**
 * <b>WARNING - This is experimental - use at own risk!</b> Builds an HTMLPage object from an HTML document. This behaves
 * similarly to the FastPageParser, however it's:
 *
 * <ul>
 * <li>easier customize to add new features such as extracting/transform custom elements.</li>
 * <li>slower - the tradeoff for flexibility.</li>
 * </ul>
 *
 * @see TokenizedHTMLPage
 * @see HTMLTagTokenizer
 *
 * @author Joe Walnes
 */
public class HTMLPageParser implements PageParser {

    public Page parse(char[] data) throws IOException {
        TokenizedHTMLPage result = new TokenizedHTMLPage(data);
        HTMLTagTokenizer tokenizer = new HTMLTagTokenizer(data);
        tokenizer.start(result);
        return result;
    }

}
