/*
 * IF YOU ARE HAVING TROUBLE COMPILING THIS CLASS, IT IS PROBABLY BECAUSE Lexer.java IS MISSING.
 *
 * Use 'ant jflex' to generate the file.
 */

package com.opensymphony.module.sitemesh.html.tokenizer;

import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.Text;
import com.opensymphony.module.sitemesh.html.util.CharArray;
import com.opensymphony.module.sitemesh.util.CharArrayReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Looks for patterns of tokens in the Lexer and translates these to calls to pass to the TokenHandler.
 *
 * @author Joe Walnes
 * @see TagTokenizer
 */
class Parser extends Lexer implements Text, Tag {

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

    private final char[] input;

    private TokenHandler handler;

    private int position;
    private int length;

    private String name;
    private int type;
    private final List attributes = new ArrayList();

    public Parser(char[] input, TokenHandler handler) {
        super(new CharArrayReader(input));
        this.input = input;
        this.handler = handler;
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
            int next;
            if (pushbackToken == -1) {
                next = yylex();
            } else {
                next = pushbackToken;
                pushbackToken = -1;
            }
            if (next != Parser.WHITESPACE) {
                pushBack(next);
                break;
            }
        }
    }

    private void pushBack(int next) {
        if (pushbackToken != -1) {
            reportError("Cannot pushback more than once", line(), column());
        }
        pushbackToken = next;
        if (next == Parser.WORD || next == Parser.QUOTED || next == Parser.SLASH || next == Parser.EQUALS) {
            pushbackText = yytext();
        } else {
            pushbackText = null;
        }
    }

    public void start() {
        try {
            while (true) {
                int token;
                if (pushbackToken == -1) {
                    token = yylex();
                } else {
                    token = pushbackToken;
                    pushbackToken = -1;
                }
                if (token == 0) {
                    // EOF
                    return;
                } else if (token == Parser.TEXT) {
                    // Got some text
                    parsedText(position(), length());
                } else if (token == Parser.LT) {
                    // Token "<" - start of tag
                    parseTag();
                } else {
                    reportError("Unexpected token from lexer, was expecting TEXT or LT", line(), column());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseTag() throws IOException {
        // Start parsing a TAG

        int start = position();
        skipWhiteSpace();
        int token;
        if (pushbackToken == -1) {
            token = yylex();
        } else {
            token = pushbackToken;
            pushbackToken = -1;
        }
        int type = Tag.OPEN;
        String name;

        if (token == Parser.SLASH) {
            // Token "/" - it's a closing tag
            type = Tag.CLOSE;
            if (pushbackToken == -1) {
                token = yylex();
            } else {
                token = pushbackToken;
                pushbackToken = -1;
            }
        }

        if (token == Parser.WORD) {
            // Token WORD - name of tag
            name = text();

            if (handler.shouldProcessTag(name)) {
                parseFullTag(type, name, start);
            } else {

                // don't care about this tag... scan to the end and treat it as text
                while(true)  {
                    if (pushbackToken == -1) {
                        token = yylex();
                    } else {
                        token = pushbackToken;
                        pushbackToken = -1;
                    }
                    if (token == Parser.GT) {
                        parsedText(start, position() - start + 1);
                        break;
                    }
                }
            }

        } else if (token == Parser.GT) {
            // Token ">" - an illegal <> or <  > tag. Ignore
        } else {
            reportError("Could not recognise tag", line(), column());
        }
    }

    private void parseFullTag(int type, String name, int start) throws IOException {
        int token;
        while (true) {
            skipWhiteSpace();
            if (pushbackToken == -1) {
                token = yylex();
            } else {
                token = pushbackToken;
                pushbackToken = -1;
            }
            pushBack(token);

            if (token == Parser.SLASH || token == Parser.GT) {
                break; // no more attributes here
            } else if (token == Parser.WORD) {
                parseAttribute(); // start of an attribute
            } else {
                reportError("XXY", line(), column());
            }
        }

        if (pushbackToken == -1) {
            token = yylex();
        } else {
            token = pushbackToken;
            pushbackToken = -1;
        }
        if (token == Parser.SLASH) {
            // Token "/" - it's an empty tag
            type = Tag.EMPTY;
            if (pushbackToken == -1) {
                token = yylex();
            } else {
                token = pushbackToken;
                pushbackToken = -1;
            }
        }

        if (token == Parser.GT) {
            // Token ">" - YAY! end of tag.. process it!
            parsedTag(type, name, start, position() - start + 1);
        } else {
            reportError("Expected end of tag", line(), column());
        }
    }

    private void parseAttribute() throws IOException {
        int token;
        if (pushbackToken == -1) {
            token = yylex();
        } else {
            token = pushbackToken;
            pushbackToken = -1;
        }
        // Token WORD - start of an attribute
        String attributeName = text();
        skipWhiteSpace();
        if (pushbackToken == -1) {
            token = yylex();
        } else {
            token = pushbackToken;
            pushbackToken = -1;
        }
        if (token == Parser.EQUALS) {
            // Token "=" - the attribute has a value
            skipWhiteSpace();
            if (pushbackToken == -1) {
                token = yylex();
            } else {
                token = pushbackToken;
                pushbackToken = -1;
            }
            if (token == Parser.QUOTED) {
                // token QUOTED - a quoted literal as the attribute value
                parsedAttribute(attributeName, text(), true);
            } else if (token == Parser.WORD || token == Parser.SLASH) {
                // unquoted word
                attributeBuffer.clear();
                attributeBuffer.append(text());
                while (true) {
                    int next;
                    if (pushbackToken == -1) {
                        next = yylex();
                    } else {
                        next = pushbackToken;
                        pushbackToken = -1;
                    }
                    if (next == Parser.WORD || next == Parser.EQUALS || next == Parser.SLASH) {
                        attributeBuffer.append(text());
                        // TODO: how to handle <a x=c/> ?
                    } else {
                        pushBack(next);
                        break;
                    }
                }
                parsedAttribute(attributeName, attributeBuffer.toString(), false);
            } else if (token == Parser.SLASH || token == Parser.GT) {
                // no more attributes
                pushBack(token);
            } else {
                reportError("Illegal attribute value", line(), column());
            }
        } else if (token == Parser.SLASH || token == Parser.GT || token == Parser.WORD) {
            // it was a value-less HTML style attribute
            parsedAttribute(attributeName, null, false);
            pushBack(token);
        } else {
            reportError("Illegal attribute name", line(), column());
        }
    }

    public void parsedText(int position, int length) {
        this.position = position;
        this.length = length;
        handler.text((Text) this);
    }

    public void parsedTag(int type, String name, int start, int length) {
        this.type = type;
        this.name = name;
        this.position = start;
        this.length = length;
        handler.tag((Tag) this);
        attributes.clear();
    }

    public void parsedAttribute(String name, String value, boolean quoted) {
        attributes.add(name);
        if (quoted) {
            attributes.add(value.substring(1, value.length() - 1));
        } else {
            attributes.add(value);
        }
    }

    protected void reportError(String message, int line, int column) {
//        System.out.println(message);
        handler.warning(message, line, column);
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String getContents() {
        return new String(input, position, length);
    }

    public void writeTo(CharArray out) {
        out.append(input, position, length);
    }

    public int getAttributeCount() {
        return attributes == null ? 0 : attributes.size() / 2;
    }

    public String getAttributeName(int index) {
        return (String) attributes.get(index * 2);
    }

    public String getAttributeValue(int index) {
        return (String) attributes.get(index * 2 + 1);
    }

    public String getAttributeValue(String name) {
        // todo: optimize
        if (attributes == null) {
            return null;
        }
        final int len = attributes.size();
        for (int i = 0; i < len; i+=2) {
            if (name.equalsIgnoreCase((String) attributes.get(i))) {
                return (String) attributes.get(i + 1);
            }
        }
        return null;
    }

    public boolean hasAttribute(String name) {
        return getAttributeValue(name) != null;
    }

}
