package com.opensymphony.module.sitemesh.parser.html;

import com.opensymphony.module.sitemesh.util.CharArray;

import java.io.IOException;
import java.io.Reader;

public class Parser extends Lexer {

    private final HTMLTagTokenizer tokenizer;
    private final CharArray attributeBuffer = new CharArray(64);
    private int pushbackToken = -1;
    private String pushbackText;

    public final static short SLASH=257;
    public final static short WHITESPACE=258;
    public final static short EQUALS=259;
    public final static short QUOTE=260;
    public final static short WORD=261;
    public final static short TEXT=262;
    public final static short QUOTED=263;
    public final static short LT=264;
    public final static short GT=265;

    public Parser(HTMLTagTokenizer tokenizer, Reader input) {
        super(input);
        this.tokenizer = tokenizer;
    }

    private String text() {
        if (pushbackToken == -1) {
            return yytext();
        } else {
            return pushbackText;
        }
    }

    private void skipWhiteSpace() throws IOException {
        while (true) {
            int next = takeNextToken();
            if (next != Parser.WHITESPACE) {
                pushBack(next);
                break;
            }
        }
    }

    private void pushBack(int next) {
        if (pushbackToken != -1) {
            fatal("Cannot pushback more than once");
        }
        pushbackToken = next;
        if (next == Parser.WORD || next == Parser.QUOTED || next == Parser.SLASH || next == Parser.EQUALS) {
            pushbackText = yytext();
        } else {
            pushbackText = null;
        }
    }

    private int takeNextToken() throws IOException {
        if (pushbackToken == -1) {
            int result = yylex();
            if (result == 0) {
                throw new IOException();
            } else {
                return result;
            }
        } else {
            int result = pushbackToken;
            pushbackToken = -1;
            pushbackText = null;
            return result;
        }
    }

    protected void reportError(String message, int line, int column) {
//        System.out.println(message);
        tokenizer.error(message, line, column);
    }

    private void fatal(String message) {
        tokenizer.error(message, line(), column());
        throw new RuntimeException(message);
    }

    public void start() {
        try {
            while (true) {
                int token = takeNextToken();
                if (token == 0) {
                    // EOF
                    return;
                } else if (token == Parser.TEXT) {
                    // Got some text
                    tokenizer.parsedText(position(), length());
                } else if (token == Parser.LT) {
                    // Token "<" - start of tag
                    parseTag();
                } else {
                    fatal("Unexpected token from lexer, was expecting TEXT or LT");
                }
            }
        } catch (IOException e) {

        }
    }

    private void parseTag() throws IOException {
        // Start parsing a TAG

        int start = position();
        skipWhiteSpace();
        int token = takeNextToken();
        int type = Tag.OPEN;
        String name;

        if (token == Parser.SLASH) {
            // Token "/" - it's a closing tag
            type = Tag.CLOSE;
            token = takeNextToken();
        }

        if (token == Parser.WORD) {
            // Token WORD - name of tag
            name = text();

            while (true) {
                skipWhiteSpace();
                token = takeNextToken();
                pushBack(token);

                if (token == Parser.SLASH || token == Parser.GT) {
                    break; // no more attributes here
                } else if (token == Parser.WORD) {
                    parseAttribute(); // start of an attribute
                } else {
                    fatal("XXY");
                }
            }

            token = takeNextToken();
            if (token == Parser.SLASH) {
                // Token "/" - it's an empty tag
                type = Tag.EMPTY;
                token = takeNextToken();
            }

            if (token == Parser.GT) {
                // Token ">" - YAY! end of tag.. process it!
                tokenizer.parsedTag(type, name, start, position() + 1 - start);
            } else {
                fatal("Expected end of tag");
            }

        } else if (token == Parser.GT) {
            // Token ">" - an illegal <> or <  > tag. Ignore
        } else {
            fatal("Could not recognise tag"); // TODO: this should be recoverable
        }
    }

    private void parseAttribute() throws IOException {
        int token = takeNextToken();
        // Token WORD - start of an attribute
        String attributeName = text();
        skipWhiteSpace();
        token = takeNextToken();
        if (token == Parser.EQUALS) {
            // Token "=" - the attribute has a value
            skipWhiteSpace();
            token = takeNextToken();
            if (token == Parser.QUOTED) {
                // token QUOTED - a quoted literal as the attribute value
                tokenizer.parsedAttribute(attributeName, text(), true);
            } else if (token == Parser.WORD || token == Parser.SLASH) {
                // unquoted word
                attributeBuffer.clear();
                attributeBuffer.append(text());
                while (true) {
                    int next = takeNextToken();
                    if (next == Parser.WORD || next == Parser.EQUALS || next == Parser.SLASH) {
                        attributeBuffer.append(text());
                        // TODO: how to handle <a x=c/> ?
                    } else {
                        pushBack(next);
                        break;
                    }
                }
                tokenizer.parsedAttribute(attributeName, attributeBuffer.toString(), false);
            } else if (token == Parser.SLASH || token == Parser.GT) {
                // no more attributes
                pushBack(token);
            } else {
                fatal("Illegal attribute value"); // TODO: recover
            }
        } else if (token == Parser.SLASH || token == Parser.GT || token == Parser.WORD) {
            // it was a value-less HTML style attribute
            tokenizer.parsedAttribute(attributeName, null, false);
            pushBack(token);
        } else {
            fatal("Illegal attribute name"); // TODO: recover
        }
    }

}
