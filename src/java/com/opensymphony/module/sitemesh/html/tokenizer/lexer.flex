/*
 * This is the definition (lexer.flex) for the auto-generated lexer (Lexer.java) created by JFlex <http://jflex.de/>.
 * To regenerate Lexer.java, run 'ant jflex'.
 *
 * @author Joe Walnes
 */

// class headers
package com.opensymphony.module.sitemesh.html.tokenizer;
%%

// class and lexer definitions
%class Lexer
%abstract
%unicode
%byaccj
%char
%line
%column
%ignorecase

%{
    // Additional methods to add to generated Lexer to aid in error reporting.
    protected int position() { return yychar; }
    protected int length()   { return yy_markedPos - yy_startRead; }
    protected int line()     { return yyline; }
    protected int column()   { return yycolumn; }
    protected abstract void reportError(String message, int line, int column);
%}

/* Additional states that the lexer can switch into. */
%state ELEMENT

%%

/* Initial state of lexer. */
<YYINITIAL> {
    "<!--" [^\[] ~"-->" { return Parser.TEXT; } /* All comments unless they start with <!--[ */
    "<!---->"           { return Parser.TEXT; } 
    "<?" ~"?>"          { return Parser.TEXT; }
    "<!" [^\[\-] ~">"     { return Parser.TEXT; }
    "<![CDATA[" ~"]]>"  { return Parser.TEXT; }
    "<xmp" ~"</xmp" ~">" { return Parser.TEXT; }
    "<script" ~"</script" ~">" { return Parser.TEXT; }
    [^<]+               { return Parser.TEXT; }
    "<"                 { yybegin(ELEMENT); return Parser.LT; }
    "<!--["             { yybegin(ELEMENT); return Parser.LT_OPEN_MAGIC_COMMENT; }
    "<!["               { yybegin(ELEMENT); return Parser.LT_CLOSE_MAGIC_COMMENT; }
}

/* State of lexer when inside an element/tag. */
<ELEMENT> {
    "/"                 { return Parser.SLASH; }
    [\n\r \t\b\012]+    { return Parser.WHITESPACE; }
    "="                 { return Parser.EQUALS; }
    "\"" ~"\""          { return Parser.QUOTED; }
    "'" ~"'"            { return Parser.QUOTED; }
    [^>\]/=\"\'\n\r \t\b\012][^>\]/=\n\r \t\b\012]* { return Parser.WORD; }
    ">"                 { yybegin(YYINITIAL); return Parser.GT; }
    "]>"                { yybegin(YYINITIAL); return Parser.GT; }
    "]-->"              { yybegin(YYINITIAL); return Parser.GT; }
}

/* Fallback rule - if nothing else matches. */
.|\n                    { reportError("Illegal character <"+ yytext() +">", line(), column()); return Parser.TEXT; }
