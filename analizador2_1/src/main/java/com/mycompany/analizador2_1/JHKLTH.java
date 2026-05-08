package com.mycompany.analizador2_1;

import java.util.HashMap;

public class JHKLTH {
    private String cont;
    private String resultado;
    private String id;
    private HashMap<String, String> variables;


    public JHKLTH(String id,String cont,HashMap<String, String> variables) throws Exception {
        this.cont = cont;
        this.id=id;
        this.variables=variables;
        Verificar();
        
    }

    public void Verificar() throws Exception {

        cont = cont.replaceAll("\\s+", "");
        resultado = evaluarExpresion(cont);
        variables.put(id, String.valueOf(resultado));
    }

    private String evaluarExpresion(String expr) throws Exception {

    java.util.List<String> valores = new java.util.ArrayList<>();
    int i = 0;

    while (i < expr.length()) {

        if (expr.charAt(i) == '°') {
            i++;
            StringBuilder str = new StringBuilder();

            while (i < expr.length() && expr.charAt(i) != '°') {
                str.append(expr.charAt(i));
                i++;
            }

            if (i >= expr.length()) {
                throw new Exception("Falta cerrar °");
            }

            i++;
            valores.add(str.toString());
            continue;
        }

        else if (Character.isLetter(expr.charAt(i))) {
            StringBuilder palabra = new StringBuilder();

            while (i < expr.length() &&
                  (Character.isLetterOrDigit(expr.charAt(i)) || expr.charAt(i) == '_')) {
                palabra.append(expr.charAt(i));
                i++;
            }

            String var = palabra.toString();

            if (!variables.containsKey(var)) {
                throw new Exception("Variable no existe: " + var);
            }

            valores.add(variables.get(var));
            continue;
        }

        else if (expr.startsWith(":)", i)) {
            i += 2;
            continue;
        }

        else {
            throw new Exception("Operador no permitido en: " + expr.charAt(i));
        }
    }

    if (valores.isEmpty()) {
        throw new Exception("Expresión vacía");
    }

    StringBuilder resultado = new StringBuilder();

    for (String s : valores) {
        resultado.append(s);
    }

    return resultado.toString();
}
    public String imprimir() throws Exception {
    StringBuilder sb = new StringBuilder();
    int i = 0;

    while (i < cont.length()) {

        if (cont.charAt(i) == '°') {
            i++;
            StringBuilder str = new StringBuilder();

            while (i < cont.length() && cont.charAt(i) != '°') {
                str.append(cont.charAt(i));
                i++;
            }

            if (i >= cont.length()) {
                throw new Exception("Error en impresión: falta cerrar °");
            }

            i++;
            sb.append("Cadena:"+str).append("\n");
            continue;
        }

        else if (Character.isLetter(cont.charAt(i))) {
            StringBuilder palabra = new StringBuilder();

            while (i < cont.length() &&
                  (Character.isLetterOrDigit(cont.charAt(i)) || cont.charAt(i) == '_')) {
                palabra.append(cont.charAt(i));
                i++;
            }

            String var = palabra.toString();

            if (!variables.containsKey(var)) {
                throw new Exception("Variable no existe: " + var);
            }

            String valor = variables.get(var);

            sb.append("variable: "+var);
            sb.append(" valor: ").append(valor).append("\n");

            continue;
        }

        else if (cont.startsWith(":)", i)) {
            sb.append("operador: :)\n");
            i += 2;
        }

        else {
            throw new Exception("Error en impresión en posición " + i);
        }
    }

    sb.append("~ ").append(resultado);

    return sb.toString();
}
}
   