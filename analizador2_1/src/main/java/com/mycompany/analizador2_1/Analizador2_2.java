package com.mycompany.analizador2_1;
/**/
import java.util.regex.*;
import java.util.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Analizador2_2 {

    public String palabra;
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

        

        if (line.equals("")) throw new Exception("Error esta vacio");
        if (line.matches("\\|[^|].*") || line.equals("|")) {
    throw new Exception("Error ponga || para un comentario");
}
        if (line.matches("\\|\\|\\s*.*")) {
    return "Comentario: " + line;
}
        if (!line.matches("(JHKLTH|LTALYV|KVIRL).*")) {
            throw new Exception("Error en palabra clave");
        }

        if (!line.matches("(JHKLTH|LTALYV|KVIRL)\\s+[a-zA-Z_]\\w*.*")) {
            throw new Exception("Error en ID");
           
        }else{
            
        }

        if (!line.matches("(JHKLTH|LTALYV|KVIRL)\\s+[a-zA-Z_]+\\w*\\s*~.*")) {
            throw new Exception("Falta ~");
        }

        if (!line.matches(".*\\^\\s*$")) {
            throw new Exception("Falta ^ al final");
        }

        Pattern p = Pattern.compile(
            "(JHKLTH|LTALYV|KVIRL)\\s+([a-zA-Z_]+\\w*)\\s*(~)\\s*(.+?)\\s*(\\^)"
        );

        Matcher m = p.matcher(line);

        if (m.matches()) {
            palabra = m.group(1);
            id = m.group(2);
            if (variables.containsKey(id)) {
                throw new Exception("Variable ya existe: " + id);
            }else if (id.equals("JHKLTH") || id.equals("LTALYV") || id.equals("KVIRL") ||id.equals("~")||id.equals("°")||id.equals("^")) {
                throw new Exception("El ID no puede ser palabra reservada: " + id);
            }
            simbolo = m.group(3);
            cont = m.group(4);
            fin = m.group(5);
            
        } else {
            throw new Exception("Error de sintaxis general");
        }

       

      

        if (variables.containsKey(id)) {
            throw new Exception("Error: variable ya declarada -> " + id);
        }
          verificarcont();

        

        return imprimir();
    }

   
    public void verificarcont() throws Exception {

        if (palabra.equals("LTALYV")) {
            tipo = "Entero";
            impcont = new LTALYV(id,cont,variables).imprimir();

        } else if (palabra.equals("KVIRL")) {
            tipo = "Decimal";
            impcont = new KVIRL(id,cont,variables).imprimir();

        } else {
            tipo = "Cadena";
            impcont = new JHKLTH(id,cont,variables).imprimir();
        }
    }

   

    public String imprimir() {

        StringBuilder sb = new StringBuilder();

        sb.append("Validacion correcta\n");
        sb.append("Palabra reservada = ").append(palabra)
          .append(" es de tipo: ").append(tipo).append("\n");
        sb.append("El id es: ").append(id).append("\n");
        sb.append("El simbolo es: ").append(simbolo).append("\n");
        sb.append("El contenido: ").append(cont).append("\n");
        sb.append(impcont).append("\n");
        sb.append("El marcador del final: ").append(fin);

        return sb.toString();
    }
    public void imprimirtable(JTable table, String line) {
        
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        if (model.getColumnCount() != 4) {
            model.setColumnIdentifiers(new Object[]{
                "TOKEN", "LEXEMA", "PATRON", "RESERVADA"
            });
        }
        
        if (line.matches("\\|\\|\\s*.*")) {
    
           model.addRow(new Object[]{
            "COMENTARIO",
            "||",
            "[\\w\\d]*",
            "SI"
        });
            model.addRow(new Object[]{
            "///////////////",
            "///////////////",
            "///////////////",
            "///////////////"
        });
            return;
        }
        
        Pattern p = Pattern.compile(
            "(JHKLTH|LTALYV|KVIRL)|" +
            "[a-zA-Z_]\\w*|" +
            "\\d+(\\.\\d+)?|" +
            "~|\\^|" +
            ":\\)|:\\(|:/|\\*\\-\\*|" +
            "°[^°]*°"
        );

        Matcher m = p.matcher(line);

        while (m.find()) {

            String lexema = m.group();

            String token = "";
            String patron = "";
            String reservada = "NO";

            if (lexema.equals("JHKLTH") || lexema.equals("LTALYV") || lexema.equals("KVIRL")
                || lexema.equals("~") || lexema.equals("^")) {

                token = "RESERVADA";
                patron = lexema;
                reservada = "SI";
            }

            else if (lexema.equals(":)") || lexema.equals(":(")
                  || lexema.equals(":/") || lexema.equals("*-*")) {

                token = "OPERADOR";
                patron = lexema;
            }

            // 🔹 NÚMEROS
            else if (lexema.matches("\\d+(\\.\\d+)?")) {
                token = "NUMERO";
                patron = "\\d+";
            }

            // 🔹 IDENTIFICADOR
            else if (lexema.matches("[a-zA-Z_]\\w*")) {
                token = "ID";
                patron = "[a-zA-Z_]\\w*";
            }

            else if (lexema.startsWith("°")) {
                token = "CADENA";
                patron = "°[^°]*°";
            }

            model.addRow(new Object[]{
                token,
                lexema,
                patron,
                reservada
            });
        }

        model.addRow(new Object[]{
            "///////////////",
            "///////////////",
            "///////////////",
            "///////////////"
        });
    }
   
}