package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.parser.tokenizer.TagTokenizer;

import java.io.IOException;

/**
 * <b>WARNING - This is experimental - use at own risk!</b> Builds an HTMLPage object from an HTML document. This behaves
 * similarly to the FastPageParser, however it's a complete rewrite that is simpler to add custom features to such as
 * extraction and transformation of elements.
 *
 * @see TokenizedHTMLPage
 * @see TagTokenizer
 *
 * @author Joe Walnes
 */
public class HTMLPageParser implements PageParser {

    public Page parse(char[] data) throws IOException {
        TokenizedHTMLPage result = new TokenizedHTMLPage(data);
        TagTokenizer tokenizer = new TagTokenizer(data);
        tokenizer.start(result);
        return result;
    }

}
