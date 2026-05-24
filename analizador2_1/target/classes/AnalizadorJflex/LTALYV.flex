package codigo;
import java_cup.runtime.Symbol;

%%
%class LTALYV
%public
%unicode
%cup
%line
%column

%{
private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
}
private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
}
%}

DIGITO = [0-9]
ID = [a-zA-Z_][a-zA-Z0-9_]*

%%

"LTALYV" { return symbol(sym.ENTERO, yytext()); }
":)"    { return symbol(sym.SUMA, "+"); }
":("  { return symbol(sym.RESTA, "-"); }
"*-*"  { return symbol(sym.MULT, "*"); }
":/" { return symbol(sym.DIV, "/"); }
"~"       { return symbol(sym.IGUAL, "="); }
"^"       { return symbol(sym.PUNTO_COMA, ";"); }

{DIGITO}+ { 
    return symbol(sym.NUMERO, Integer.valueOf(yytext())); 
}
{ID}      { return symbol(sym.ID, yytext()); }

[ \t\r\n]+ { /* ignorar */ }
.          { System.out.println("Error léxico: " + yytext()); }