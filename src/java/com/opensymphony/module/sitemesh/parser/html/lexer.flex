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
    protected int line()     { return yyline; }
    protected int column()   { return yycolumn; }
    protected abstract void reportError(String message, int line, int column);
%}

%state ELEMENT


%%

<YYINITIAL> {
    "<!--" ~"-->"       { return Parser.TEXT; }
    "<?" ~"?>"          { return Parser.TEXT; }
    "<!" ~">"           { return Parser.TEXT; }
    "<![CDATA[" ~"]]>"  { return Parser.TEXT; }
    "<xmp" ~"</xmp" ~">" { return Parser.TEXT; }
    "<xml" ~"</xml" ~">" { return Parser.TEXT; }
    "<script" ~"</script" ~">" { return Parser.TEXT; }
    [^<]+               { return Parser.TEXT; }
    "<"                 { yybegin(ELEMENT); return Parser.LT; } /* Switch state to ELEMENT */
}

<ELEMENT> {
    "/"                 { return Parser.SLASH; }
    [\n\r \t\b\012]+    { return Parser.WHITESPACE; }
    "="                 { return Parser.EQUALS; }
    "\"" ~"\""          { return Parser.QUOTED; }
    "'" ~"'"            { return Parser.QUOTED; }
    [^>/=\"\'\n\r \t\b\012][^>/=\n\r \t\b\012]* { return Parser.WORD; }
    ">"                 { yybegin(YYINITIAL); return Parser.GT; } /* Switch state back to YYINITIAL */
}

/* not matched by anything else */
.|\n                    { reportError("Illegal character <"+ yytext() +">", line(), column()); return Parser.TEXT; }
