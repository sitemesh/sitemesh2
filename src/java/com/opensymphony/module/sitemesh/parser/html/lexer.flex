// class headers
package com.opensymphony.module.sitemesh.parser.html;
%%

// class and lexer definitions
%class Lexer
%abstract
%unicode
%byaccj
%line
%column
%ignorecase

%{
    protected int position() { return yy_currentPos; }
%}

%state ELEMENT

%%

<YYINITIAL> {
    "<!--" ~"-->"       { return Parser.TEXT; }
    "<?" ~"?>"          { return Parser.TEXT; }
    "<!" ~">"           { return Parser.TEXT; }
    "<![CDATA[" ~"]]>"  { return Parser.TEXT; }
    "<xmp" ~"</xmp>"    { return Parser.TEXT; }
    "<xml" ~"</xml>"    { return Parser.TEXT; }
    "<script" ~"</script>" { return Parser.TEXT; }
    [^<]+               { return Parser.TEXT; }
    "<"                 { yybegin(ELEMENT); return Parser.LT; } /* Switch state to ELEMENT */
}

<ELEMENT> {
    "/"                 { return Parser.SLASH; }
    [\n\r\ \t\b\012]+   { return Parser.WHITESPACE; }
    [^<>=\"'/ ]+        { return Parser.WORD; }
    "="                 { return Parser.EQUALS; }
    "\"" ~"\""          { return Parser.QUOTED; }
    "'" ~"'"            { return Parser.QUOTED; }
    ">"                 { yybegin(YYINITIAL); return Parser.GT; } /* Switch state back to YYINITIAL */
}

/* not matched by anything else */
.|\n                    { throw new Error("Illegal character <"+ yytext()+"> Line " + yyline + ", Column " + yycolumn); }