package rs.ac.bg.etf.pp1;

import org.apache.log4j.*;
import java_cup.runtime.*;
%%

%{
	private Logger log = Logger.getLogger(getClass());

	// ukljucivanje informacije o poziciji tokena
	public Symbol new_symbol(int type) {
		return new Symbol(type, yyline+1, yycolumn + 1);
	}
	
	// ukljucivanje informacije o poziciji tokena
	public Symbol new_symbol(int type, Object value) {
		return new Symbol(type, yyline+1, yycolumn + 1, value);
	}
%}

%function next_token
%type java_cup.runtime.Symbol

%state COMMENT

%eofval{
	return new_symbol(sym.EOF);
%eofval}

%class MJLexer
%cup
%line
%column

%%
<COMMENT> {
"\r\n" {yybegin(YYINITIAL);}
. {yybegin(COMMENT);}
}

<YYINITIAL> {
" " {}
"\b" {}
"\t" {}
"\r\n" {}
"\f" {}
"true" | "false" {return new_symbol(sym.BOOL_CONST, new Boolean(yytext()));}
";" {return new_symbol(sym.SEMICOLON, yytext());}
"+=" {return new_symbol(sym.ADDASSIGN, yytext());}
"-=" {return new_symbol(sym.SUBASSIGN, yytext());}
"*=" {return new_symbol(sym.MULASSIGN, yytext());}
"/=" {return new_symbol(sym.DIVASSIGN, yytext());}
"%=" {return new_symbol(sym.MODASSIGN, yytext());}
"=" {return new_symbol(sym.ASSIGNMENT, yytext());}
"++" {return new_symbol(sym.INCREMENT, yytext());}
"--" {return new_symbol(sym.DECREMENT, yytext());}
"(" {return new_symbol(sym.LEFT_PARENTHESIS, yytext());}
")" {return new_symbol(sym.RIGHT_PARENTHESIS, yytext());}
"read" {return new_symbol(sym.READ, yytext());}
"print" {return new_symbol(sym.PRINT, yytext());}
"," {return new_symbol(sym.COMMA, yytext());}
"new" {return new_symbol(sym.NEW, yytext());}
">" {return new_symbol(sym.GREATER, yytext());}
"==" {return new_symbol(sym.EQUAL, yytext());}
"<" {return new_symbol(sym.LESSER, yytext());}
">=" {return new_symbol(sym.GREATER_EQUAL, yytext());}
"<=" {return new_symbol(sym.LESSER_EQUAL, yytext());}
"!=" {return new_symbol(sym.NOT_EQUAL, yytext());}
"+" {return new_symbol(sym.PLUS, yytext());}
"-" {return new_symbol(sym.MINUS, yytext());}
"*" {return new_symbol(sym.MULTIPLICATION, yytext());}
"/" {return new_symbol(sym.DIVISION, yytext());}
"%" {return new_symbol(sym.MODULO, yytext());}
"[" {return new_symbol(sym.LEFT_SQUARE, yytext());}
"]" {return new_symbol(sym.RIGHT_SQUARE, yytext());}
"if" {return new_symbol(sym.IF, yytext());}
"else" {return new_symbol(sym.ELSE, yytext());}
"for" {return new_symbol(sym.FOR, yytext());}
"break" {return new_symbol(sym.BREAK, yytext());}
"continue" {return new_symbol(sym.CONTINUE, yytext());}
"program" { return new_symbol(sym.PROGRAM, yytext());}
"const" { return new_symbol(sym.CONST, yytext());}
"void" { return new_symbol(sym.VOID, yytext());}
"return" {return new_symbol(sym.RETURN, yytext());}
"{" {return new_symbol(sym.LEFT_BRACE, yytext());}
"}" {return new_symbol(sym.RIGHT_BRACE, yytext());}
"||" {return new_symbol(sym.OR, yytext());}
"&&" {return new_symbol(sym.AND, yytext());}
"//" {yybegin(COMMENT);}
"~"|"!"|"?"|"^"|"$"|"#"|"&"|"|"|([0-9]+[a-z|A-Z|_])|"`"|":"|"@"|"\\" {log.error("Leksicka greska za simbol \"" + yytext() + "\" na liniji " + (yyline + 1) + ", koloni " + (yycolumn + 1) + "!");}
([a-z]|[A-Z])[a-z|A-Z|0-9|_]* {return new_symbol(sym.IDENT, yytext());}
[0-9]+ {return new_symbol(sym.NUM_CONST, new Integer(yytext()));}
"'"[\040-\176]"'" {return new_symbol(sym.CHAR_CONST, new Character(yytext().charAt(1)));}
}