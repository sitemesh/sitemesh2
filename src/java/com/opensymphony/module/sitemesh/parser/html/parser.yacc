/*
 * This is the definition (parser.yacc) for the auto-generated lexer (Parser.java)
 * created by BYacc/J <http://byaccj.sourceforge.net/>.
 * To regenerate Parser.java, run 'ant yacc'.
 *
 * @author Joe Walnes
 */


/********** TOKEN DEFINITIONS (produced by lexer) *********/

%token SLASH WHITESPACE EQUALS QUOTE
%token <sval> WORD TEXT QUOTED    /* string contains text */
%token <ival> LT GT               /* int contains position */
%type <sval> unquoted
%%



/********** GRAMMAR (BNF form, with Java snippets to execute on match) *********/

document:
    document node |
    /*blank*/     ;

node:
    LT WORD attributes GT            /* <tag ...> */              { tokenizer.parsedTag(Tag.OPEN,  $2, $1, $4 + 1); } |
    LT SLASH WORD attributes GT      /* </tag ...> */             { tokenizer.parsedTag(Tag.CLOSE, $3, $1, $5 + 1); } |
    LT WORD attributes SLASH GT      /* <tag ... /> */            { tokenizer.parsedTag(Tag.EMPTY, $2, $1, $5 + 1); } |
    LT whitespace GT                 /* < > (strip error) */      { } |    /* TODO, I think this is wrong, <> is valid in VBScript */
    LT error GT                      /* Malformed tag - text */   { tokenizer.parsedText($1, $3 + 1);   } |
    TEXT                             /* Text */                   { tokenizer.parsedText($1);           } ;

attributes:
    attributes WORD whitespace EQUALS whitespace unquoted whitespace { tokenizer.parsedAttribute($2, $6  , false); } | /* a=b   */
    attributes WORD whitespace EQUALS whitespace QUOTED   whitespace { tokenizer.parsedAttribute($2, $6  , true);  } | /* a="b" */
    attributes WORD whitespace                                       { tokenizer.parsedAttribute($2, null, false); } | /* a     */
    whitespace ;

unquoted:  /* This is needed to deal with the annoying special cases <a something=nasty/slash href=blah?x=y> */
    unquoted WORD   { $$ = $1 + $2; } |
    unquoted SLASH  { $$ = $1 + "/"; } |
    unquoted EQUALS { $$ = $1 + "="; } |
    /*blank*/       { $$ = ""; };

whitespace:
    whitespace WHITESPACE |
    /*blank*/             ;



/********** ADDITIONAL JAVA TO MIX INTO PARSER *********/
%%
private HTMLTagTokenizer tokenizer;

public Parser(HTMLTagTokenizer tokenizer, java.io.Reader input) {
    super(input);
    this.tokenizer = tokenizer;
}

public int yylex() {
    try {
        final int result = super.yylex();
        yylval = new Value();
        yylval.sval = yytext();
        yylval.ival = position();
        yylval.line = line();
        yylval.column = column();
        return result;
    }
    catch(java.io.IOException e) {
        return 0;
    }
}

public void yyerror(String error) {
    Value value = val_peek(0);
    reportError(error, value.line, value.column);
}

protected void reportError(String message, int line, int column) {
    tokenizer.error(message, line, column);
}

private static class Value {
    String sval;
    int ival;
    int line;
    int column;
}
