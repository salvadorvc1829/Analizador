package com.mycompany.analizador2_1;

import java.util.HashMap;

public class KVIRL {

    private String cont;
    private double resultado;
    private String id;
    private HashMap<String, String> variables;

    public KVIRL(String id, String cont, HashMap<String, String> variables) throws Exception {
        this.cont = cont;
        this.id = id;
        this.variables = variables;
        Verificar();
    }

    public void Verificar() throws Exception {

        cont = cont.replaceAll("\\s+", "");

        resultado = evaluarExpresion(cont);

        variables.put(id, String.valueOf(resultado));
    }

    private Double evaluarExpresion(String expr) throws Exception {

        java.util.Stack<Double> numeros = new java.util.Stack<>();
        java.util.Stack<Character> ops = new java.util.Stack<>();
        int i = 0;

        while (i < expr.length()) {

            // ================= NUMEROS =================
            // Ahora SOLO acepta números con punto obligatorio:
            // Ejemplos válidos: 3.00, 12.5, 0.25
            // Ejemplos inválidos: 3, 25, 100
            if (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.') {

                String num = "";
                int countInt = 0;
                int countDec = 0;
                boolean decimal = false;
                int puntos = 0;

                while (i < expr.length()
                        && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {

                    if (expr.charAt(i) == '.') {
                        decimal = true;
                        puntos++;
                    } else {
                        if (!decimal) {
                            countInt++;
                        } else {
                            countDec++;
                        }
                    }

                    num += expr.charAt(i);
                    i++;
                }

                // Debe existir exactamente un punto decimal
                if (puntos != 1) {
                    throw new Exception("Error: los numeros decimales deben contener un punto (ejemplo: 3.00)");
                }

                // Debe haber al menos un dígito después del punto
                if (countDec == 0) {
                    throw new Exception("Error: debe haber al menos un digito despues del punto decimal");
                }

                // Máximo 10 dígitos enteros
                if (countInt > 10) {
                    throw new Exception("Error: mas de 10 digitos enteros");
                }

                // Máximo 8 dígitos decimales
                if (countDec > 8) {
                    throw new Exception("Error: mas de 8 decimales");
                }

                numeros.push(Double.parseDouble(num));
                continue;
            }

            // ================= VARIABLES =================
            // SOLO acepta variables cuyo valor tenga punto decimal obligatorio
            else if (Character.isLetter(expr.charAt(i))) {

                String palabra = "";

                while (i < expr.length()
                        && (Character.isLetterOrDigit(expr.charAt(i)) || expr.charAt(i) == '_')) {
                    palabra += expr.charAt(i);
                    i++;
                }

                if (variables.containsKey(palabra)) {

                    String valor = variables.get(palabra);

                    // Debe ser decimal con punto obligatorio
                    // Ejemplos válidos: 3.0, 10.25, 123.456
                    // Ejemplos inválidos: 3, hola
                    if (valor != null && valor.matches("\\d{1,10}\\.\\d{1,8}")) {
                        numeros.push(Double.parseDouble(valor));
                    } else {
                        throw new Exception("Error: la variable (" + palabra + ") no es decimal");
                    }

                } else {
                    throw new Exception("Variable no existe: " + palabra);
                }

                continue;
            }

            // ================= STRING NO PERMITIDO =================
            else if (expr.charAt(i) == '°') {
                throw new Exception("Contenido invalido: no acepta Strings (KVIRL)");
            }

            // ================= OPERADORES =================
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
                throw new Exception("Contenido invalido: " + expr.charAt(i));
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

    private void aplicarOperacion(java.util.Stack<Double> nums, char op) throws Exception {

        double b = nums.pop();
        double a = nums.pop();

        switch (op) {

            case '+':
                nums.push(a + b);
                break;

            case '-':
                nums.push(a - b);
                break;

            case '*':
                nums.push(a * b);
                break;

            case '/':
                if (b == 0) {
                    throw new Exception("División por cero");
                }
                nums.push(a / b);
                break;
        }
    }

    public String imprimir() throws Exception {

        StringBuilder sb = new StringBuilder();
        int i = 0;

        while (i < cont.length()) {

            if (Character.isDigit(cont.charAt(i)) || cont.charAt(i) == '.') {

                String num = "";

                while (i < cont.length()
                        && (Character.isDigit(cont.charAt(i)) || cont.charAt(i) == '.')) {

                    num += cont.charAt(i);
                    i++;
                }

                sb.append("constante: ").append(num).append("\n");
                continue;
            }

            else if (Character.isLetter(cont.charAt(i))) {

                StringBuilder palabra = new StringBuilder();

                while (i < cont.length()
                        && (Character.isLetterOrDigit(cont.charAt(i)) || cont.charAt(i) == '_')) {

                    palabra.append(cont.charAt(i));
                    i++;
                }

                String var = palabra.toString();

                if (!variables.containsKey(var)) {
                    throw new Exception("Variable no existe: " + var);
                }

                String valor = variables.get(var);

                // Solo variables decimales con punto obligatorio
                if (!valor.matches("\\d{1,10}\\.\\d{1,8}")) {
                    throw new Exception("Error: variable no es decimal");
                }

                sb.append("variable: ").append(var)
                  .append(" valor: ").append(valor).append("\n");

                continue;
            }

            else if (cont.startsWith(":)", i)) {
                sb.append("operador: :)\n");
                i += 2;
            }

            else if (cont.startsWith(":(", i)) {
                sb.append("operador: :(\n");
                i += 2;
            }

            else if (cont.startsWith(":/", i)) {
                sb.append("operador: :/\n");
                i += 2;
            }

            else if (cont.startsWith("*-*", i)) {
                sb.append("operador: *-*\n");
                i += 3;
            }

            else {
                throw new Exception("Error en impresión en posición " + i);
            }
        }

        sb.append("resultado ~ ").append(resultado);
        return sb.toString();
    }
}