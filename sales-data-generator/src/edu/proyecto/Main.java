package edu.proyecto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Main (compatibilidad con generadores de terceros)
 * ------------------------------------------------
 * Lee los archivos de entrada desde ./data/input y genera reportes en ./data/output.
 * - Detecta automáticamente nombres de archivos en español o inglés.
 * - Mapea cabeceras alternativas de columnas (es/en) sin pedir datos al usuario.
 */
public class Main {

    private static final Path INPUT_DIR = Paths.get("data", "input");
    private static final Path OUTPUT_DIR = Paths.get("data", "output");

    public static void main(String[] args) {
        try {
            Files.createDirectories(OUTPUT_DIR);

            Path sellersFile  = firstExisting(INPUT_DIR, "sellers.csv", "vendedores.csv");
            Path productsFile = firstExisting(INPUT_DIR, "products.csv", "productos.csv");
            Path salesFile    = firstExisting(INPUT_DIR, "sales.csv", "ventas.csv");

            if (sellersFile == null || productsFile == null || salesFile == null) {
                throw new IOException("No se encontraron todos los archivos requeridos en " + INPUT_DIR.toAbsolutePath()
                    + ". Esperados: sellers.csv/vendedores.csv, products.csv/productos.csv, sales.csv/ventas.csv");
            }

            List<Seller> sellers = readSellers(sellersFile);
            List<Product> products = readProducts(productsFile);
            List<Sale> sales = readSales(salesFile);

            Map<String, Seller> sellersById = new HashMap<>();
            for (Seller s : sellers) sellersById.put(s.id, s);
            Map<String, Product> productsById = new HashMap<>();
            for (Product p : products) productsById.put(p.id, p);

            Map<String, SellerAgg> aggBySeller = new HashMap<>();
            Map<String, ProductAgg> aggByProduct = new HashMap<>();

            for (Sale sale : sales) {
                Product p = productsById.get(sale.productId);
                if (p == null) continue;
                double lineTotal = p.unitPrice * sale.quantity;

                SellerAgg sa = aggBySeller.computeIfAbsent(sale.sellerId, k -> new SellerAgg());
                sa.units += sale.quantity;
                sa.revenue += lineTotal;
                sa.productUnits.merge(sale.productId, sale.quantity, Integer::sum);

                ProductAgg pa = aggByProduct.computeIfAbsent(sale.productId, k -> new ProductAgg());
                pa.units += sale.quantity;
                pa.revenue += lineTotal;
                pa.sellersUnits.merge(sale.sellerId, sale.quantity, Integer::sum);
            }

            Path r1 = OUTPUT_DIR.resolve("report_sales_by_seller.csv");
            try (BufferedWriter bw = Files.newBufferedWriter(r1, StandardCharsets.UTF_8)) {
                bw.write("seller_id,seller_name,total_units,total_revenue,distinct_products,top_product_id,top_product_units");
                bw.newLine();
                List<Map.Entry<String,SellerAgg>> rows = new ArrayList<>(aggBySeller.entrySet());
                Collections.sort(rows, Comparator.comparingDouble((Map.Entry<String,SellerAgg> e) -> e.getValue().revenue).reversed());
                for (Map.Entry<String,SellerAgg> e : rows) {
                    String sid = e.getKey();
                    Seller s = sellersById.get(sid);
                    SellerAgg sa = e.getValue();
                    String topProd = topKey(sa.productUnits);
                    int topUnits = sa.productUnits.getOrDefault(topProd, 0);
                    bw.write(sid + "," + csv(s == null ? "DESCONOCIDO" : s.name) + "," + sa.units + "," +
                             String.format(Locale.US, "%.2f", sa.revenue) + "," + sa.productUnits.size() + "," +
                             (topProd == null ? "" : topProd) + "," + topUnits);
                    bw.newLine();
                }
            }

            Path r2 = OUTPUT_DIR.resolve("report_products_by_quantity.csv");
            try (BufferedWriter bw = Files.newBufferedWriter(r2, StandardCharsets.UTF_8)) {
                bw.write("product_id,product_name,total_units,total_revenue,distinct_sellers,top_seller_id,top_seller_units");
                bw.newLine();
                List<Map.Entry<String,ProductAgg>> rows = new ArrayList<>(aggByProduct.entrySet());
                Collections.sort(rows, Comparator.comparingInt((Map.Entry<String,ProductAgg> e) -> e.getValue().units).reversed());
                for (Map.Entry<String,ProductAgg> e : rows) {
                    String pid = e.getKey();
                    Product p = productsById.get(pid);
                    ProductAgg pa = e.getValue();
                    String topSeller = topKey(pa.sellersUnits);
                    int topUnits = pa.sellersUnits.getOrDefault(topSeller, 0);
                    bw.write(pid + "," + csv(p == null ? "DESCONOCIDO" : p.name) + "," + pa.units + "," +
                             String.format(Locale.US, "%.2f", pa.revenue) + "," + pa.sellersUnits.size() + "," +
                             (topSeller == null ? "" : topSeller) + "," + topUnits);
                    bw.newLine();
                }
            }

            Path r3 = OUTPUT_DIR.resolve("resumen.txt");
            try (BufferedWriter bw = Files.newBufferedWriter(r3, StandardCharsets.UTF_8)) {
                bw.write("RESUMEN DE PROCESAMIENTO\n");
                bw.write("------------------------\n");
                bw.write("Fecha de ejecución: " + LocalDate.now() + "\n\n");
                bw.write("Registros leídos:\n");
                bw.write("  - Sellers: " + sellers.size() + "\n");
                bw.write("  - Products: " + products.size() + "\n");
                bw.write("  - Sales: " + sales.size() + "\n\n");
                bw.write("Reportes generados en: " + OUTPUT_DIR.toAbsolutePath() + "\n");
            }

            System.out.println("OK: Reportes generados en " + OUTPUT_DIR.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("ERROR: No fue posible generar los reportes. " + e.getMessage());
            e.printStackTrace();
        }
    }

    /* ==================== MODELOS ==================== */

    static final class Seller {
        final String id;
        final String name;
        Seller(String id, String name) { this.id = id; this.name = name; }
    }

    static final class Product {
        final String id;
        final String name;
        final String category;
        final double unitPrice;
        Product(String id, String name, String category, double unitPrice) {
            this.id = id; this.name = name; this.category = category; this.unitPrice = unitPrice;
        }
    }

    static final class Sale {
        final String saleId;
        final String sellerId;
        final String productId;
        final int quantity;
        final String date; // ISO yyyy-MM-dd
        Sale(String saleId, String sellerId, String productId, int quantity, String date) {
            this.saleId = saleId; this.sellerId = sellerId; this.productId = productId; this.quantity = quantity; this.date = date;
        }
    }

    /* ==================== AGREGADOS ==================== */

    static final class SellerAgg {
        int units = 0;
        double revenue = 0.0;
        Map<String,Integer> productUnits = new HashMap<>();
    }

    static final class ProductAgg {
        int units = 0;
        double revenue = 0.0;
        Map<String,Integer> sellersUnits = new HashMap<>();
    }

    /* ==================== IO CSV ==================== */

    private static List<Seller> readSellers(Path path) throws IOException {
        List<Seller> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String header = br.readLine();
            if (header == null) throw new IOException("Archivo vacío: " + path);
            String[] cols = splitHeader(header);
            int iId   = indexOf(cols, "seller_id", "idvendedor", "vendedor_id", "id", "sellerid");
            int iName = indexOf(cols, "seller_name", "nombre", "vendedor", "seller_name");
            String line;
            while ((line = br.readLine()) != null) {
                String[] a = parseCSV(line, cols.length);
                out.add(new Seller(a[iId], a[iName]));
            }
        }
        return out;
    }

    private static List<Product> readProducts(Path path) throws IOException {
        List<Product> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String header = br.readLine();
            if (header == null) throw new IOException("Archivo vacío: " + path);
            String[] cols = splitHeader(header);
            int iId    = indexOf(cols, "product_id", "producto_id", "idproducto", "id_prod", "id");
            int iName  = indexOf(cols, "product_name", "nombre", "nombre_producto", "producto");
            int iCat   = indexOf(cols, "category", "categoria", "rubro");
            int iPrice = indexOf(cols, "unit_price", "precio", "precio_unitario", "price");
            String line;
            while ((line = br.readLine()) != null) {
                String[] a = parseCSV(line, cols.length);
                out.add(new Product(a[iId], a[iName], a[iCat], Double.parseDouble(a[iPrice])));
            }
        }
        return out;
    }

    private static List<Sale> readSales(Path path) throws IOException {
        List<Sale> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String header = br.readLine();
            if (header == null) throw new IOException("Archivo vacío: " + path);
            String[] cols = splitHeader(header);
            int iId     = indexOf(cols, "sale_id", "venta_id", "idventa", "id");
            int iSeller = indexOf(cols, "seller_id", "vendedor_id", "idvendedor", "sellerid");
            int iProd   = indexOf(cols, "product_id", "producto_id", "idproducto", "productid");
            int iQty    = indexOf(cols, "quantity", "cantidad", "qty");
            int iDate   = indexOf(cols, "date", "fecha", "fecha_venta");
            String line;
            while ((line = br.readLine()) != null) {
                String[] a = parseCSV(line, cols.length);
                out.add(new Sale(a[iId], a[iSeller], a[iProd], Integer.parseInt(a[iQty]), a[iDate]));
            }
        }
        return out;
    }

    private static String[] parseCSV(String line, int expectedMin) {
        // CSV parser con soporte de comillas dobles
        List<String> cols = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '\"') {
                    if (i+1 < line.length() && line.charAt(i+1) == '\"') {
                        sb.append('\"'); i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    sb.append(c);
                }
            } else {
                if (c == ',') {
                    cols.add(sb.toString()); sb.setLength(0);
                } else if (c == '\"') {
                    inQuotes = true;
                } else {
                    sb.append(c);
                }
            }
        }
        cols.add(sb.toString());
        if (cols.size() < expectedMin) {
            throw new IllegalArgumentException("CSV inválido: columnas=" + cols.size() + " < esperadas " + expectedMin + ". Línea: " + line);
        }
        return cols.toArray(new String[0]);
    }

    private static String csv(String s) {
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private static String[] splitHeader(String header) {
        // split simple por comas; no esperamos comillas en cabecera
        String[] cols = header.split(",", -1);
        for (int i = 0; i < cols.length; i++) cols[i] = cols[i].trim().toLowerCase();
        return cols;
    }

    private static int indexOf(String[] cols, String... options) {
        for (int i = 0; i < cols.length; i++) {
            String c = cols[i];
            for (String opt : options) {
                if (c.equals(opt.toLowerCase())) return i;
            }
        }
        throw new IllegalArgumentException("No se encontró columna: " + String.join("/", options));
    }

    private static <K> String topKey(Map<String,Integer> m) {
        String best = null;
        int max = -1;
        for (Map.Entry<String,Integer> e : m.entrySet()) {
            if (e.getValue() > max) { max = e.getValue(); best = e.getKey(); }
        }
        return best;
    }

    private static Path firstExisting(Path dir, String... names) {
        for (String n : names) {
            Path p = dir.resolve(n);
            if (Files.exists(p)) return p;
        }
        return null;
    }
}
