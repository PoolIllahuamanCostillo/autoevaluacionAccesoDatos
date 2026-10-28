package es.fplumara.dam.ad.examen;

import es.fplumara.dam.ad.examen.modelo.Anuncio;
import es.fplumara.dam.ad.examen.modelo.Vendedor;
import tools.jackson.databind.ObjectMapper; // Librería Jackson 3 (Visto en P08 y P09)

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Principal {

    // Definimos las rutas relativas (igual que en P03 y P05)
    private static final String RUTA_CSV = "datos/anuncios.csv";
    private static final String RUTA_JSON_ANUNCIOS = "datos/anuncios.json";
    private static final String RUTA_JSON_VENDEDORES = "datos/vendedores.json";

    public static void main(String[] args) {

        // Lista donde guardaremos los objetos Anuncio convertidos desde el CSV
        List<Anuncio> listaAnuncios = new ArrayList<>();

        // ---- Apartado 2 -----
        // Usamos un LinkedHashMap para almacenar vendedores garantizando que sean ÚNICOS por su ID (Apartado 2)
        // (P09 / Colecciones Java)
        Map<Integer, Vendedor> mapVendedores = new LinkedHashMap<>();

        boolean contieneVendedores = false;
        // ---------------------

        System.out.println("=== LEYENDO FICHERO CSV ===");

        // 1. Abrimos el BufferedReader con UTF-8 dentro de un try-with-resources (P03 / P09)
        // El try-with-resources se encarga de cerrar el fichero automáticamente al terminar.
        try (BufferedReader lector = new BufferedReader(new FileReader(RUTA_CSV, StandardCharsets.UTF_8))) {

            // Leemos la primera línea para inspeccionar la cabecera
            String cabecera = lector.readLine();

            if (cabecera != null) {
                String[] camposCabecera = cabecera.split(";");

                // DETECCIÓN AUTOMÁTICA (Apartado 2): Si tiene más de 9 campos, incluye vendedores
                if (camposCabecera.length > 9) {
                    contieneVendedores = true;
                    System.out.println("-> Se ha detectado automáticamente información de VENDEDOR.");
                } else {
                    System.out.println("-> CSV estándar detectado (sin vendedores).");
                }
            }

            // Leemos la primera línea de datos real
            String linea = lector.readLine();
            int numLinea = 2; // Empezamos a contar en la línea 2 para los mensajes de error

            while (linea != null) {
                if (!linea.isBlank()) { // Ignoramos líneas vacías por seguridad (P03)

                    // Separamos la línea por el punto y coma (;)
                    String[] campos = linea.split(";");

                    try {
                        // Convertimos cada campo de texto a su tipo correspondiente
                        // Extracción de los datos del Anuncio (Apartado 1)
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

                        // APARTADO 2:
                        // Extraer datos del vendedor solo si la cabecera los incluía
                        // Posiciones en el CSV: 9 (vendedor_id), 10 (vendedor_nombre),
                        // 11 (vendedor_apellidos), 12 (vendedor_cp)
                        if (contieneVendedores && campos.length >= 13) {
                            int idVendedor = Integer.parseInt(campos[9].trim());
                            String nombre = campos[10].trim();
                            String apellidos = campos[11].trim();
                            int cp = Integer.parseInt(campos[12].trim());

                            Vendedor vendedor = new Vendedor(idVendedor, nombre, apellidos, cp);

                            // putIfAbsent solo lo inserta si la clave (idVendedor) NO existe previamente
                            mapVendedores.putIfAbsent(idVendedor, vendedor);
                        }

                    } catch (Exception e) {
                        // Si una línea viene mal formada, avisamos pero no detenemos el programa (Patrón de P03)
                        System.out.println("Advertencia: Línea " + numLinea + " con formato incorrecto descartada.");
                    }
                }
                // Leemos la siguiente línea del bucle
                linea = lector.readLine();
                numLinea++;
            }

            System.out.println("-> Anuncios leídos: " + listaAnuncios.size());

        } catch (IOException e) {
            System.err.println("Error al leer el archivo CSV: " + e.getMessage());
            return;
        }

        // APARTADO 3: Filtrar interactiva por Categoría
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== APARTADO 3: FILTRAR POR CATEGORÍA ===");
        System.out.print("Introduce una categoría (ej: Móviles, Bicis y patinetes, Hogar) o 'Todos': ");
        String categoriaElegida = scanner.nextLine().trim();

        List<Anuncio> anunciosAExportar = new ArrayList<>();

        if (categoriaElegida.equalsIgnoreCase("Todos") || categoriaElegida.isBlank()) {
            anunciosAExportar = listaAnuncios;
            System.out.println("-> Se exportarán TODAS las categorías (" + anunciosAExportar.size() + " anuncios).");
        } else {
            for (Anuncio a : listaAnuncios) {
                if (a.getCategoria().equalsIgnoreCase(categoriaElegida)) {
                    anunciosAExportar.add(a);
                }
            }
            System.out.println("-> Categoría '" + categoriaElegida + "' seleccionada: " + anunciosAExportar.size() + " anuncios encontrados.");
        }

        // 2. Exportación a JSON con Jackson 3 (P08 / P09)
        ObjectMapper mapper = new ObjectMapper();

        // Guardamos anuncios.json
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(RUTA_JSON_VENDEDORES), listaAnuncios);
        System.out.println("[OK] Fichero '" + RUTA_JSON_ANUNCIOS + "' generado con éxito.");

        // APARTADO 2: Guardar vendedores.json SOLO si el CSV contenía vendedores
        if (contieneVendedores) {
            // Obtenemos únicamente los valores del Map (que ya son únicos)
            List<Vendedor> listaVendedoresUnicos = new ArrayList<>(mapVendedores.values());

            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(RUTA_JSON_VENDEDORES), listaVendedoresUnicos);
            System.out.println("[OK] Fichero '" + RUTA_JSON_VENDEDORES + "' generado con " + listaVendedoresUnicos.size() + " vendedores únicos.");
        }

    }
}