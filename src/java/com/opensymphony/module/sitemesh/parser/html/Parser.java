//### This file created by BYACC 1.8(/Java extension  1.1)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//### Please send bug reports to rjamison@lincom-asg.com
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";



package com.opensymphony.module.sitemesh.parser.html;







/**
 * Encapsulates yacc() parser functionality in a Java
 *        class for quick code development
 */
public class Parser
             extends Lexer
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[],stateptr;           //state stack
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
void state_push(int state)
{
  if (stateptr>=YYSTACKSIZE)         //overflowed?
    return;
  statestk[++stateptr]=state;
  if (stateptr>statemax)
    {
    statemax=state;
    stateptrmax=stateptr;
    }
}
int state_pop()
{
  if (stateptr<0)                    //underflowed?
    return -1;
  return statestk[stateptr--];
}
void state_drop(int cnt)
{
int ptr;
  ptr=stateptr-cnt;
  if (ptr<0)
    return;
  stateptr = ptr;
}
int state_peek(int relative)
{
int ptr;
  ptr=stateptr-relative;
  if (ptr<0)
    return -1;
  return statestk[ptr];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
boolean init_stacks()
{
  statestk = new int[YYSTACKSIZE];
  stateptr = -1;
  statemax = -1;
  stateptrmax = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//## **user defined:Value
String   yytext;//user variable to return contextual strings
Value yyval; //used to return semantic vals from action routines
Value yylval;//the 'lval' (result) I got from yylex()
Value valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new Value[YYSTACKSIZE];
  yyval=new Value();
  yylval=new Value();
  valptr=-1;
}
void val_push(Value val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
Value val_pop()
{
  if (valptr<0)
    return null;
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
Value val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return null;
  return valstk[ptr];
}
//#### end semantic value section ####
public final static short SLASH=257;
public final static short WHITESPACE=258;
public final static short EQUALS=259;
public final static short QUOTE=260;
public final static short WORD=261;
public final static short TEXT=262;
public final static short QUOTED=263;
public final static short LT=264;
public final static short GT=265;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    2,    2,    2,    2,    2,    4,    4,    4,
    4,    1,    1,    1,    5,    5,    3,
};
final static short yylen[] = {                            2,
    2,    1,    4,    5,    5,    3,    1,    7,    7,    3,
    1,    2,    2,    1,    2,    1,    0,
};
final static short yydefred[] = {                         0,
    7,    0,    0,    0,    2,    0,    0,    0,   16,    0,
    1,    0,   15,    0,   11,    6,    0,    0,    0,    3,
    4,    5,    0,    0,    0,    0,    0,   14,    9,   13,
   12,    8,
};
final static short yydgoto[] = {                          3,
   27,    4,    9,   14,   15,
};
final static short yysindex[] = {                      -261,
    0, -238,    0, -261,    0, -257, -237, -237,    0, -234,
    0, -237,    0, -228,    0,    0, -247, -218, -237,    0,
    0,    0, -207, -237, -235, -237, -208,    0,    0,    0,
    0,    0,
};
final static short yyrindex[] = {                        51,
    0, -211,    0,   51,    0,    0, -250, -219,    0,    0,
    0, -220,    0,    0,    0,    0,    0,    0, -249,    0,
    0,    0, -217, -231, -222, -219, -211,    0,    0,    0,
    0,    0,
};
final static short yygindex[] = {                        52,
    0,    0,    2,   43,   -2,
};
final static int YYTABLESIZE=56;
final static short yytable[] = {                         10,
    1,    5,    2,   12,   13,    5,   17,   17,   17,   17,
   17,   17,   17,   19,   17,   17,   23,   21,    6,    7,
    7,   25,    8,   29,   32,   17,   28,   26,   18,   17,
   16,   17,   19,   17,   17,   17,   20,   17,   17,   10,
   17,   17,   17,   10,   17,   17,   22,   10,   30,    7,
   17,   24,   31,   17,   17,   11,
};
final static short yycheck[] = {                          2,
  262,    0,  264,  261,    7,    4,  257,  257,  259,  259,
  261,  261,  263,  261,  265,  265,   19,  265,  257,  258,
  258,   24,  261,   26,   27,  257,   25,  263,  257,  261,
  265,  263,  261,  265,  257,  258,  265,  257,  261,  257,
  261,  261,  265,  261,  265,  265,  265,  265,  257,  258,
    0,  259,  261,  265,   12,    4,
};
final static short YYFINAL=3;
final static short YYMAXTOKEN=265;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"SLASH","WHITESPACE","EQUALS","QUOTE","WORD","TEXT","QUOTED",
"LT","GT",
};
final static String yyrule[] = {
"$accept : document",
"document : node document",
"document : empty",
"node : LT WORD attributes GT",
"node : LT SLASH WORD attributes GT",
"node : LT WORD attributes SLASH GT",
"node : LT whitespace GT",
"node : TEXT",
"attributes : attributes WORD whitespace EQUALS whitespace unquoted whitespace",
"attributes : attributes WORD whitespace EQUALS whitespace QUOTED whitespace",
"attributes : attributes WORD whitespace",
"attributes : whitespace",
"unquoted : unquoted WORD",
"unquoted : unquoted SLASH",
"unquoted : empty",
"whitespace : WHITESPACE whitespace",
"whitespace : empty",
"empty :",
};

//#line 49 "parser.yacc"
private HTMLTagTokenizer tokenizer;
private int start;
private int end;

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
        return result;
    }
    catch(java.io.IOException e) {
        return 0;
    }
}

/* error reporting */
public void yyerror(String error) {
    throw new RuntimeException("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+"), text " + yytext + ", error " + error);
}

private class Value {
    String sval;
    int ival;
}
//#line 237 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 3:
//#line 20 "parser.yacc"
{ tokenizer.parsedTag(Tag.OPEN,  val_peek(2).sval, val_peek(3).ival, val_peek(0).ival + 1); }
break;
case 4:
//#line 21 "parser.yacc"
{ tokenizer.parsedTag(Tag.CLOSE, val_peek(2).sval, val_peek(4).ival, val_peek(0).ival + 1); }
break;
case 5:
//#line 22 "parser.yacc"
{ tokenizer.parsedTag(Tag.EMPTY, val_peek(3).sval, val_peek(4).ival, val_peek(0).ival + 1); }
break;
case 6:
//#line 23 "parser.yacc"
{ }
break;
case 7:
//#line 24 "parser.yacc"
{ tokenizer.parsedText(val_peek(0).sval);           }
break;
case 8:
//#line 27 "parser.yacc"
{ tokenizer.parsedAttribute(val_peek(5).sval, val_peek(1).sval  , false); }
break;
case 9:
//#line 28 "parser.yacc"
{ tokenizer.parsedAttribute(val_peek(5).sval, val_peek(1).sval  , true);  }
break;
case 10:
//#line 29 "parser.yacc"
{ tokenizer.parsedAttribute(val_peek(1).sval, null, false); }
break;
case 12:
//#line 33 "parser.yacc"
{ yyval.sval = val_peek(1).sval + val_peek(0).sval; }
break;
case 13:
//#line 34 "parser.yacc"
{ yyval.sval = val_peek(1).sval + "/"; }
break;
case 14:
//#line 35 "parser.yacc"
{ yyval.sval = ""; }
break;
//#line 428 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
//## The -Jnorun option was used ##
//## end of method run() ########################################



//## Constructors ###############################################
//## The -Jnoconstruct option was used ##
//###############################################################



}
//################### END OF CLASS ##############################
