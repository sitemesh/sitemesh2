package com.opensymphony.module.sitemesh.html.rules;

import com.opensymphony.module.sitemesh.html.TextFilter;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * TextFilter that substitutes content using a JDK 1.4 regular expression.
 *
 * <h3>Example</h3>
 *
 * This will substitute 'Google:Blah' for a link to a google search.
 *
 * <pre>HTMLProcessor processor = new HTMLProcessor(in, out);
 * processor.addTextFilter(new RegexReplacementTextFilter("Google:([a-zA-Z]+)", "<a href='http://www.google.com/q=$1'>$1</a>"));
 * // add more TextFilters and TagRules
 * processor.process();</pre>
 *
 * @author Joe Walnes
 */
public class RegexReplacementTextFilter implements TextFilter {

    private final Pattern regex;
    private final String replacement;

    public RegexReplacementTextFilter(String regex, String replacement) {
        this.regex = Pattern.compile(regex);
        this.replacement = replacement;
    }

    public RegexReplacementTextFilter(Pattern regex, String replacement) {
        this.regex = regex;
        this.replacement = replacement;
    }

    public String filter(String text) {
        Matcher matcher = regex.matcher(text);
        return matcher.replaceAll(replacement);
    }

}
