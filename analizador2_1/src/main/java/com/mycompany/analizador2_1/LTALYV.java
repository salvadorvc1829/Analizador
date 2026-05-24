package com.mycompany.analizador2_1;

import java.util.HashMap;

public class LTALYV {
    private String cont;
    private int resultado;
    private String id;
    private HashMap<String, String> variables;

    public LTALYV(String id,String cont ,HashMap<String, String> variables) throws Exception {
        this.cont = cont;
        this.id=id;
        this.variables=variables;
        Verificar();
        
    }

    public void Verificar() throws Exception {

        cont = cont.replaceAll("\\s+", "");   
        resultado = evaluarExpresion(cont);
        String comf= String.valueOf(resultado);
        if (!comf.matches("\\d{1,10}")) {
            throw new Exception("Error matematico: El resultado es mayor a 10 digitos");
        }
        variables.put(id, comf);
    }

    private int evaluarExpresion(String expr) throws Exception {
        java.util.Stack<Integer> numeros = new java.util.Stack<>();
        java.util.Stack<Character> ops = new java.util.Stack<>();
        int i = 0;

        while (i < expr.length()) {

            if (Character.isDigit(expr.charAt(i))) {
                int num = 0;
                while (i < expr.length() && Character.isDigit(expr.charAt(i))) {
                    num = num * 10 + (expr.charAt(i) - '0');
                    i++;
                }
                
                numeros.push(num);
                continue;
            }else if(Character.isLetter(expr.charAt(i))){
                String palabra = "";
                while (i < expr.length() && Character.isLetter(expr.charAt(i))) {
                   palabra += expr.charAt(i);i++;
                }
                if(variables.containsKey(palabra)){
                    String valor = variables.get(palabra);
                  if(valor != null && valor.matches("\\d+")){
                    numeros.push(Integer.parseInt(valor));
                  }else{
                    throw new Exception("Variable no es del mismo tipo");
                  }
                }else {
                throw new Exception("Contenido invalido: "+palabra);
            }
                
                continue;
            }else if(expr.charAt(i)==('.')){
                 throw new Exception("Contenido invalido: no acepta decimales(JHKLTH)");
            }else if(expr.charAt(i)==('°')){
                throw new Exception("Contenido invalido: no acepta Strings(KVIRL)");
            }

            String op;

            if (expr.startsWith(":)", i)) {
                op = "+";
                i += 2;
            } else if (expr.startsWith(":(", i)) {
                op = "-";
                i += 2;
            } else if (expr.startsWith(":/", i)) {
                op = "/";
                i += 2;
            } else if (expr.startsWith("*-*", i)) {
                op = "*";
                i += 3;
            } else {
                throw new Exception("Contenido invalido: "+expr.charAt(i));
            }
            if (i >= expr.length()) {
                throw new Exception("Error: Expresión incompleta. Falta un operando después del operador '" + op + "'");
            }
            char c = op.charAt(0);

            while (!ops.isEmpty() && prioridad(ops.peek()) >= prioridad(c)) {
                aplicarOperacion(numeros, ops.pop());
            }

            ops.push(c);
        }

        while (!ops.isEmpty()) {
            aplicarOperacion(numeros, ops.pop());
        }

        return numeros.pop();
    }

    private int prioridad(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        return 0;
    }

    private void aplicarOperacion(java.util.Stack<Integer> nums, char op) throws Exception {
        int b = nums.pop();
        int a = nums.pop();

        switch (op) {
            case '+': nums.push(a + b); break;
            case '-': nums.push(a - b); break;
            case '*': nums.push(a * b); break;
            case '/':
            if (b == 0) throw new Exception("División por cero");

            if (a % b != 0) {
                throw new Exception("División no puede dar decimal");
            }

            nums.push(a / b);
            break;
        }
    }

    public String imprimir() throws Exception {
        StringBuilder sb = new StringBuilder();
int i = 0;

while (i < cont.length()) {

    if (Character.isDigit(cont.charAt(i))) {
        StringBuilder num = new StringBuilder();
        while (i < cont.length() && Character.isDigit(cont.charAt(i))) {
            num.append(cont.charAt(i));
            i++;
        }
        sb.append("constante: "+num).append("\n");
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

        if (!valor.matches("\\d+")) {
            throw new Exception("Error: La variable (" + var + ") no es tipo entero");
        }

        sb.append("variable: "+var);
        sb.append(" valor: ").append(valor).append("\n");

        continue;
    }

    else if (cont.startsWith(":)", i)) {
        sb.append("operador: :)\n");
        i += 2;
    } else if (cont.startsWith(":(", i)) {
        sb.append("operador: :(\n");
        i += 2;
    } else if (cont.startsWith(":/", i)) {
        sb.append("operador: :/\n");
        i += 2;
    } else if (cont.startsWith("*-*", i)) {
        sb.append("operador: *-*\n");
        i += 3;
    } else {
        throw new Exception("Error en impresión en posición " + i);
    }
    
}
 sb.append("resultado ~ ").append(resultado);
return sb.toString();
    }
}


  
    