package AnalizadorJflex;

import java.io.*;

public class Principal {

    public static void main(String[] args) {

        // Leer siempre desde consola
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {

            while (true) {

                System.out.println("Escribe código (o escribe salir):");

                // Leer línea escrita en consola
                String codigo = br.readLine();

                // Salir del programa
                if (codigo.equalsIgnoreCase("salir")) {
                    System.out.println("Programa terminado.");
                    break;
                }

                try {

                    // Crear lexer
                    JHVHJALY lexer = new JHVHJALY(new StringReader(codigo));

                    // Crear parser
                    JHVHJALYC parser = new JHVHJALYC(lexer);

                    // Ejecutar análisis
                    parser.parse();

                    System.out.println("Análisis correcto");

                } catch (Exception e) {

                    System.out.println("Error de análisis:");
                    e.printStackTrace();
                }

                System.out.println("--------------------------------");

            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}