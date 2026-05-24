package codigo;
import java_cup.runtime.Symbol;

%%
%class JHVHJALY
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

/* Macros */
DIGITO = [0-9]
ID = [a-zA-Z_][a-zA-Z0-9_]*
/* Esta es la macro para el String: comilla, cualquier cosa que no sea comilla, comilla */
CADENA_TEXTO = \" [^ \"]* \"

%%

/* Palabras Reservadas y Operaciones con Emojis */
"LTALYV" { return symbol(sym.ENTERO, yytext()); }
":)"     { return symbol(sym.SUMA, "+"); }
":("     { return symbol(sym.RESTA, "-"); }
"*-*"    { return symbol(sym.MULT, "*"); }
":/"     { return symbol(sym.DIV, "/"); }
"~"      { return symbol(sym.IGUAL, "="); }
"^"      { return symbol(sym.PUNTO_COMA, ";"); }

/* Regla para el String */
{CADENA_TEXTO} { 
    /* Quitamos las comillas del principio y del final para obtener solo el texto */
    String str = yytext();
    String contenido = str.substring(1, str.length() - 1);
    return symbol(sym.CADENA, contenido); 
}

/* Números e Identificadores */
{DIGITO}+ { 
    return symbol(sym.NUMERO, Integer.valueOf(yytext())); 
}
{ID} { 
    return symbol(sym.ID, yytext()); 
}

/* Ignorar espacios y saltos de línea */
[ \t\r\n]+ { /* ignorar */ }

/* Error léxico */
. { System.out.println("Error léxico detectado: " + yytext()); }
