package es.fplumara.dam.ad.examen;

import es.fplumara.dam.ad.examen.modelo.Anuncio;
import tools.jackson.databind.ObjectMapper; // Librería Jackson 3 (Visto en P08 y P09)

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Principal {

    // Definimos las rutas relativas (igual que en P03 y P05)
    private static final String RUTA_CSV = "datos/anuncios.csv";
    private static final String RUTA_JSON = "datos/anuncios.json";

    public static void main(String[] args) {

        // Lista donde guardaremos los objetos Anuncio convertidos desde el CSV
        List<Anuncio> listaAnuncios = new ArrayList<>();

        System.out.println("=== PASO 1: Leyendo el fichero CSV ===");

        // 1. Abrimos el BufferedReader con UTF-8 dentro de un try-with-resources (P03 / P09)
        // El try-with-resources se encarga de cerrar el fichero automáticamente al terminar.
        try (BufferedReader lector = new BufferedReader(new FileReader(RUTA_CSV, StandardCharsets.UTF_8))) {

            // Leemos la primera línea y la descartamos porque es la cabecera (id;titulo;categoria;...)
            String cabecera = lector.readLine();

            // Leemos la primera línea de datos real
            String linea = lector.readLine();
            int numLinea = 2; // Empezamos a contar en la línea 2 para los mensajes de error

            while (linea != null) {
                if (!linea.isBlank()) { // Ignoramos líneas vacías por seguridad (P03)

                    // Separamos la línea por el punto y coma (;)
                    String[] campos = linea.split(";");

                    try {
                        // Convertimos cada campo de texto a su tipo correspondiente
                        int id = Integer.parseInt(campos[0].trim());
                        String titulo = campos[1].trim();
                        String categoria = campos[2].trim();
                        int precio = Integer.parseInt(campos[3].trim());
                        String estado = campos[4].trim();
                        String barrio = campos[5].trim();
                        String fechaPublicacion = campos[6].trim();
                        int visitas = Integer.parseInt(campos[7].trim());
                        boolean vendido = Boolean.parseBoolean(campos[8].trim());

                        // Creamos el objeto Anuncio y lo añadimos a la lista en memoria
                        Anuncio anuncio = new Anuncio(id, titulo, categoria, precio, estado, barrio, fechaPublicacion, visitas, vendido);
                        listaAnuncios.add(anuncio);

                    } catch (Exception e) {
                        // Si una línea viene mal formada, avisamos pero no detenemos el programa (Patrón de P03)
                        System.out.println("Advertencia: Línea " + numLinea + " con formato incorrecto descartada.");
                    }
                }
                // Leemos la siguiente línea del bucle
                linea = lector.readLine();
                numLinea++;
            }

            System.out.println("-> Se han leído " + listaAnuncios.size() + " anuncios correctamente.");

        } catch (IOException e) {
            System.err.println("Error de entrada/salida al leer el CSV: " + e.getMessage());
            return;
        }

        // 2. Convertimos la lista de objetos Java a formato JSON usando Jackson 3 (P08 / P09)
        ObjectMapper mapper = new ObjectMapper();

            // writerWithDefaultPrettyPrinter() da formato legible (con saltos de línea y sangría)
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(RUTA_JSON), listaAnuncios);
            System.out.println("[OK] Fichero '" + RUTA_JSON + "' generado con éxito.");
    }
}