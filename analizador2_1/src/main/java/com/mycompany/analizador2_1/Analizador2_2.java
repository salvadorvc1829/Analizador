package com.mycompany.analizador2_1;

import java.util.regex.*;
import java.util.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Analizador2_2 {

    public String palabra = null;
    public String id;
    public String cont;
    public String fin;
    public String simbolo;
    public String tipo;
    public String impcont;
    public String impcont2;

    private HashMap<String, String> variables = new HashMap<>();

    public Analizador2_2() {
        variables.clear();
    }

    public String VerificarL(String line) throws Exception {

        if (line == null || line.trim().isEmpty()) {
            throw new Exception("Error: linea vacia");
        }

        line = line.trim();

      
        if (line.endsWith("^")) {
            line = line.substring(0, line.length() - 1).trim();
        }

        if (line.matches("\\|\\|\\s*.*")) {
            return "Comentario: " + line;
        }

        // IF (om)
        if (line.startsWith("om")) {

            Pattern pif = Pattern.compile(
                "om\\s*\\((.+?)\\)\\s*\\{([\\s\\S]*)\\}"
            );

            Matcher m = pif.matcher(line);

            if (!m.matches()) {
                throw new Exception("Sintaxis IF no válida");
            }

            String condicion = m.group(1).trim();
            String bloque = m.group(2).trim();

            // quitar llave final si viene pegada
            if (bloque.endsWith("}")) {
                bloque = bloque.substring(0, bloque.length() - 1).trim();
            }

            Pattern comp = Pattern.compile("(.+?)(==|!=|>=|<=|>|<)(.+)");
            Matcher mc = comp.matcher(condicion);

            if (!mc.matches()) {
                throw new Exception("Condición inválida");
            }

            String izq = mc.group(1).trim();
            String der = mc.group(3).trim();

            String tipoIzq = obtenerTipo(izq);
            String tipoDer = obtenerTipo(der);

            if (!tipoIzq.equals(tipoDer)) {
                throw new Exception("Error de tipos en IF");
            }

            // EJECUTAR BLOQUE DEL IF
            StringBuilder salida = new StringBuilder();

            String[] lineas = bloque.split("\\r?\\n");

            for (String l : lineas) {

                l = l.trim();

                if (l.isEmpty()) continue;

                if (l.endsWith("^")) {
                    l = l.substring(0, l.length() - 1).trim();
                }

                salida.append(VerificarL(l)).append("\n");
            }

            return "IF valido\n" + salida.toString();
        }

        if (line.matches("[a-zA-Z_]\\w*\\s+(ltalyv|kvirl|jhklth)\\s*~.+")) {

            Pattern p = Pattern.compile(
                "([a-zA-Z_]\\w*)\\s+(ltalyv|kvirl|jhklth)\\s*~\\s*(.+)"
            );

            Matcher m = p.matcher(line);

            if (!m.matches()) {
                throw new Exception("Error de sintaxis");
            }

            id = m.group(1);
            palabra = m.group(2).toUpperCase();
            cont = m.group(3);

            if (variables.containsKey(id)) {
                throw new Exception("Variable ya existe: " + id);
            }

            verificarcont();
            return imprimir();
        }

        else if (line.matches("[a-zA-Z_]\\w*\\s*~.+")) {

            Pattern p = Pattern.compile(
                "([a-zA-Z_]\\w*)\\s*~\\s*(.+)"
            );

            Matcher m = p.matcher(line);

            if (!m.matches()) {
                throw new Exception("Error de sintaxis");
            }

            id = m.group(1);
            cont = m.group(2);

            if (!variables.containsKey(id)) {
                throw new Exception("Variable no existe: " + id);
            }

            palabra = detectarTipo(variables.get(id));

            verificarcont();
            return imprimir();
        }

        throw new Exception("Sintaxis no válida");
    }

    private String obtenerTipo(String val) throws Exception {

        if (variables.containsKey(val)) {
            String v = variables.get(val);

            if (v.matches("\\d+")) return "LTALYV";
            if (v.matches("\\d+\\.\\d+")) return "KVIRL";
            return "JHKLTH";
        }

        if (val.matches("\\d+")) return "LTALYV";
        if (val.matches("\\d+\\.\\d+")) return "KVIRL";
        if (val.matches("°[^°]*°")) return "JHKLTH";

        throw new Exception("Variable no existe: " + val);
    }

    private String detectarTipo(String v) {
        if (v.matches("\\d+")) return "LTALYV";
        if (v.matches("\\d+\\.\\d+")) return "KVIRL";
        return "JHKLTH";
    }

    public void verificarcont() throws Exception {

    if (palabra.equals("LTALYV")) {

        tipo = "Entero";
        impcont = "constante: " + cont;

    } else if (palabra.equals("KVIRL")) {

        tipo = "Decimal";
        impcont = new KVIRL(id, cont, variables).imprimir();

    } else {

        tipo = "Cadena";
        impcont = new JHKLTH(id, cont, variables).imprimir();
    }
}
    

    public String imprimir() {

    StringBuilder sb = new StringBuilder();

    sb.append("Validacion correcta\n");

    // ======================================
    // IF
    // ======================================

    if (palabra != null && palabra.equals("OM")) {

        sb.append("Palabra reservada = om")
          .append(" es de tipo: if\n");

        sb.append("El simbolo es: (){}\n");

        sb.append("El contenido: ").append(cont).append("\n");

        sb.append(impcont).append("\n");

        return sb.toString();
    }

    // ======================================
    // VARIABLES NORMALES
    // ======================================

    if (palabra != null) {

        sb.append("Palabra reservada = ")
          .append(palabra)
          .append(" es de tipo: ")
          .append(tipo)
          .append("\n");
    }

    sb.append("El id es: ").append(id).append("\n");

    // ======================================
    // SIMBOLO
    // ======================================

    sb.append("El simbolo es: ~\n");

    sb.append("El contenido: ").append(cont).append("\n");

    sb.append(impcont).append("\n");

    // ======================================
    // FINAL
    // ======================================

    sb.append("El marcador del final: ^");

    return sb.toString();
}

    public void imprimirtable(JTable table, String line) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        if (model.getColumnCount() != 4) {

            model.setColumnIdentifiers(new Object[]{
                "TOKEN",
                "LEXEMA",
                "PATRON",
                "RESERVADA"
            });
        }

        if (line.matches("\\|\\|\\s*.*")) {

            model.addRow(new Object[]{
                "COMENTARIO",
                "||",
                "comentario",
                "SI"
            });

            return;
        }

        Pattern p = Pattern.compile(
            "(ltalyv|kvirl|jhklth|om)|" +
            "[a-zA-Z_]\\w*|" +
            "\\d+(\\.\\d+)?|" +
            "~|\\^|\\{|\\}|\\(|\\)|" +
            ":\\)|:\\(|:/|\\*\\-\\*|" +
            "==|!=|>=|<=|>|<|" +
            "°[^°]*°"
        );

        Matcher m = p.matcher(line);

        while (m.find()) {

            String lexema = m.group();

            String token = "";
            String patron = "";
            String reservada = "NO";

            if (lexema.equals("ltalyv")) {
                token = "ltalyv";
                patron = "ltalyv";
                reservada = "SI";
            }
            else if (lexema.equals("kvirl")) {
                token = "kvirl";
                patron = "kvirl";
                reservada = "SI";
            }
            else if (lexema.equals("jhklth")) {
                token = "jhklth";
                patron = "jhklth";
                reservada = "SI";
            }
            else if (lexema.equals("om")) {
                token = "if";
                patron = "om(condicion){contenido}";
                reservada = "SI";
            }
            else if (lexema.matches("==|!=|>=|<=|>|<")) {
                token = "OPERADOR";
                patron = lexema;
            }
            else if (lexema.matches("[~\\^{}()]")) {
                token = "SIMBOLO";
                patron = lexema;
            }
            else if (lexema.matches("\\d+(\\.\\d+)?")) {
                if(!lexema.matches("\\d+")){
                    token = "DECIAMAL";
                patron = "\\d+\\.\\d+";
                }else{
                   token = "Numero";
                patron = "\\d+"; 
                }
                
                
            }
            else {
                token = "ID";
                patron = lexema;
            }

            model.addRow(new Object[]{
                token,
                lexema,
                patron,
                reservada
            });
        }
    }
}