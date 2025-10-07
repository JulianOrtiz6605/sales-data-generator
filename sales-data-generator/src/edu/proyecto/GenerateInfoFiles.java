package edu.proyecto;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

/**
 * GenerateInfoFiles
 * ------------------
 * Genera archivos planos pseudoaleatorios de vendedores, productos y ventas.
 * NO solicita datos al usuario.
 * 
 * Salida (por defecto en ./data/input):
 *  - sellers.csv
 *  - products.csv
 *  - sales.csv
 * 
 * Mensajes de estado:
 *  - "OK: Archivos de entrada generados en ..." en caso de éxito
 *  - "ERROR: ..." en caso de fallo
 */
public class GenerateInfoFiles {
    // Configuración: cambia cantidades aquí si lo deseas
    private static final int SELLERS_COUNT  = 8;
    private static final int PRODUCTS_COUNT = 12;
    private static final int SALES_COUNT    = 220;

    private static final Path INPUT_DIR = Paths.get("data", "input");

    public static void main(String[] args) {
        try {
            Files.createDirectories(INPUT_DIR);
            List<Seller> sellers = RandomData.generateSellers(SELLERS_COUNT);
            List<Product> products = RandomData.generateProducts(PRODUCTS_COUNT);
            List<Sale> sales = RandomData.generateSales(SALES_COUNT, sellers, products);

            CSV.writeSellers(INPUT_DIR.resolve("sellers.csv"), sellers);
            CSV.writeProducts(INPUT_DIR.resolve("products.csv"), products);
            CSV.writeSales(INPUT_DIR.resolve("sales.csv"), sales);

            System.out.println("OK: Archivos de entrada generados en " + INPUT_DIR.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("ERROR: No fue posible generar los archivos. " + e.getMessage());
            e.printStackTrace();
        }
    }

    /* ==================== MODELOS ==================== */

    static final class Seller {
        final String id;
        final String name;

        Seller(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    static final class Product {
        final String id;
        final String name;
        final String category;
        final double unitPrice;

        Product(String id, String name, String category, double unitPrice) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.unitPrice = unitPrice;
        }
    }

    static final class Sale {
        final String saleId;
        final String sellerId;
        final String productId;
        final int quantity;
        final LocalDate date;

        Sale(String saleId, String sellerId, String productId, int quantity, LocalDate date) {
            this.saleId = saleId;
            this.sellerId = sellerId;
            this.productId = productId;
            this.quantity = quantity;
            this.date = date;
        }
    }

    /* ==================== UTILIDADES ==================== */

    static final class CSV {
        static void writeSellers(Path path, List<Seller> list) throws IOException {
            try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                bw.write("seller_id,seller_name");
                bw.newLine();
                for (Seller s : list) {
                    bw.write(s.id + "," + escape(s.name));
                    bw.newLine();
                }
            }
        }

        static void writeProducts(Path path, List<Product> list) throws IOException {
            try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                bw.write("product_id,product_name,category,unit_price");
                bw.newLine();
                for (Product p : list) {
                    bw.write(p.id + "," + escape(p.name) + "," + escape(p.category) + "," + String.format(Locale.US, "%.2f", p.unitPrice));
                    bw.newLine();
                }
            }
        }

        static void writeSales(Path path, List<Sale> list) throws IOException {
            try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                bw.write("sale_id,seller_id,product_id,quantity,date");
                bw.newLine();
                for (Sale s : list) {
                    bw.write(s.saleId + "," + s.sellerId + "," + s.productId + "," + s.quantity + "," + s.date);
                    bw.newLine();
                }
            }
        }

        private static String escape(String s) {
            if (s.contains(",") || s.contains("\"")) {
                return "\"" + s.replace("\"", "\"\"") + "\"";
            }
            return s;
        }
    }

    static final class RandomData {
        private static final String[] FIRST_NAMES = {
            "Laura","Pedro","Xiomara","Vanessa","Camila","Mateo","Luisa","Sofía","Andrés","Valentina"
        };
        private static final String[] LAST_NAMES = {
            "García","Rodríguez","Martínez","López","Gómez","Hernández","Torres","Ramírez","Castro","Morales"
        };
        private static final String[] CATEGORIES = {
            "Electrónica","Hogar","Deportes","Ropa","Jardín","Belleza","Juguetería","Alimentos"
        };
        private static final String[] PRODUCT_WORDS = {
            "Pro","Max","Lite","Ultra","Air","Go","Eco","Plus","Prime","Mini","Smart","Flex"
        };

        static List<Seller> generateSellers(int n) {
            Random r = new Random(7);
            List<Seller> out = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                String id = String.format("S%03d", i+1);
                String name = FIRST_NAMES[r.nextInt(FIRST_NAMES.length)] + " " + LAST_NAMES[r.nextInt(LAST_NAMES.length)];
                out.add(new Seller(id, name));
            }
            return out;
        }

        static List<Product> generateProducts(int n) {
            Random r = new Random(11);
            List<Product> out = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                String id = String.format("P%03d", i+1);
                String category = CATEGORIES[r.nextInt(CATEGORIES.length)];
                String name = category + " " + PRODUCT_WORDS[r.nextInt(PRODUCT_WORDS.length)];
                double price = 10 + r.nextInt(90) + r.nextDouble(); // 10.00 - 100.99
                out.add(new Product(id, name, category, Math.round(price * 100.0) / 100.0));
            }
            return out;
        }

        static List<Sale> generateSales(int n, List<Seller> sellers, List<Product> products) {
            Random r = new Random(23);
            List<Sale> out = new ArrayList<>();
            LocalDate start = LocalDate.now().minusDays(60);
            for (int i = 0; i < n; i++) {
                Seller s = sellers.get(r.nextInt(sellers.size()));
                Product p = products.get(r.nextInt(products.size()));
                int qty = 1 + r.nextInt(8);
                LocalDate date = start.plusDays(r.nextInt(60));
                out.add(new Sale(UUID.randomUUID().toString(), s.id, p.id, qty, date));
            }
            return out;
        }
    }
}
