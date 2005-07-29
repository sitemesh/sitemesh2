/*
 * IF YOU ARE HAVING TROUBLE COMPILING THIS CLASS, IT IS PROBABLY BECAUSE Lexer.java IS MISSING.
 *
 * Use 'ant jflex' to generate the file, which will reside in build/java
 */

package com.opensymphony.module.sitemesh.html.tokenizer;

import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.Text;
import com.opensymphony.module.sitemesh.html.util.CharArray;
import com.opensymphony.module.sitemesh.util.CharArrayReader;

import java.io.IOException;

/**
 * Looks for patterns of tokens in the Lexer and translates these to calls to pass to the TokenHandler.
 *
 * @author Joe Walnes
 * @see TagTokenizer
 */
public class Parser extends Lexer {

    private final CharArray attributeBuffer = new CharArray(64);
    private final ReusableToken reusableToken = new ReusableToken();

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
    public final static short LT_OPEN_MAGIC_COMMENT=266;
    public final static short LT_CLOSE_MAGIC_COMMENT=267;

    private final char[] input;

    private TokenHandler handler;

    private int position;
    private int length;

    private String name;
    private int type;

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
                    parseTag(Tag.OPEN);
                } else if (token == Parser.LT_OPEN_MAGIC_COMMENT) {
                    // Token "<!--[" - start of open magic comment
                    parseTag(Tag.OPEN_MAGIC_COMMENT);
                } else if (token == Parser.LT_CLOSE_MAGIC_COMMENT) {
                    // Token "<![" - start of close magic comment
                    parseTag(Tag.CLOSE_MAGIC_COMMENT);
                } else {
                    reportError("Unexpected token from lexer, was expecting TEXT or LT", line(), column());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseTag(int type) throws IOException {
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
            String name = text();
            if (handler.shouldProcessTag(name)) {
                parseFullTag(type, name, start);
            } else {
                resetLexerState();
                pushBack(yylex()); // take and replace the next token, so the position is correct
                parsedText(start, position() - start);
            }
        } else if (token == Parser.GT) {
            // Token ">" - an illegal <> or <  > tag. Ignore
        } else if (token == 0) {
            parsedText(start, position() - start); // eof
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
            } else if (token == 0) {
                parsedText(start, position() - start); // eof
                return;
            } else {
                reportError("Illegal tag", line(), column());
                break;
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
        } else if (token == 0) {
            parsedText(start, position() - start); // eof
        } else {
            reportError("Expected end of tag", line(), column());
            parsedTag(type, name, start, position() - start + 1);
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
                    } else {
                        pushBack(next);
                        break;
                    }
                }
                parsedAttribute(attributeName, attributeBuffer.toString(), false);
            } else if (token == Parser.SLASH || token == Parser.GT) {
                // no more attributes
                pushBack(token);
            } else if (token == 0) {
                return;
            } else {
                reportError("Illegal attribute value", line(), column());
            }
        } else if (token == Parser.SLASH || token == Parser.GT || token == Parser.WORD) {
            // it was a value-less HTML style attribute
            parsedAttribute(attributeName, null, false);
            pushBack(token);
        } else if (token == 0) {
            return;
        } else {
            reportError("Illegal attribute name", line(), column());
        }
    }

    protected void parsedText(int position, int length) {
        this.position = position;
        this.length = length;
        handler.text(reusableToken);
    }

    protected void parsedTag(int type, String name, int start, int length) {
        this.type = type;
        this.name = name;
        this.position = start;
        this.length = length;
        handler.tag(reusableToken);
        reusableToken.attributeCount = 0;
    }

    protected void parsedAttribute(String name, String value, boolean quoted) {
        if(reusableToken.attributeCount + 2 >= reusableToken.attributes.length) {
            String[] newAttributes = new String[reusableToken.attributeCount * 2];
            System.arraycopy(reusableToken.attributes, 0, newAttributes, 0, reusableToken.attributeCount);
            reusableToken.attributes = newAttributes;
        }
        reusableToken.attributes[reusableToken.attributeCount++] = name;
        if (quoted) {
            reusableToken.attributes[reusableToken.attributeCount++] = value.substring(1, value.length() - 1);
        } else {
            reusableToken.attributes[reusableToken.attributeCount++] = value;
        }
    }

    protected void reportError(String message, int line, int column) {
        handler.warning(message, line, column);
    }

    public class ReusableToken implements Tag, Text {

        public int attributeCount = 0;
        public String[] attributes = new String[10]; // name1, value1, name2, value2...
      
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
            return attributeCount / 2;
        }

        public int getAttributeIndex(String name, boolean caseSensitive) {
            if(attributeCount == 0)
               return -1;
            final int len = attributeCount;
            for (int i = 0; i < len; i+=2) {
                final String current = attributes[i];
                if (caseSensitive ? name.equals(current) : name.equalsIgnoreCase(current)) {
                    return i / 2;
                }
            }
            return -1;
        }

        public String getAttributeName(int index) {
            return attributes[index * 2];
        }

        public String getAttributeValue(int index) {
            return attributes[index * 2 + 1];
        }

        public String getAttributeValue(String name, boolean caseSensitive) {
          if(attributeCount == 0)
             return null;
          final int len = attributeCount;
          for (int i = 0; i < len; i+=2) {
              final String current = attributes[i];
              if (caseSensitive ? name.equals(current) : name.equalsIgnoreCase(current)) {
                  return attributes[i + 1];
              }
          }
          return null;
        }

        public boolean hasAttribute(String name, boolean caseSensitive) {
            return getAttributeIndex(name, caseSensitive) > -1;
        }

    }
}
