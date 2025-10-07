# Proyecto Ventas (Java)

**Requisito clave:** solo hay **dos clases con método `main`**: `GenerateInfoFiles` y `Main`.  
No se solicita información al usuario.

## Estructura
```
ProyectoVentasJava/
 ├─ src/
 │   └─ edu/proyecto/
 │       ├─ GenerateInfoFiles.java
 │       └─ Main.java
 ├─ data/
 │   ├─ input/   (se crea al generar datos)
 │   └─ output/  (se crea al generar reportes)
 └─ README.md
```

## Pasos en Eclipse EE
1. **File → New → Java Project**, nombre: `ProyectoVentasJava`.
2. Copia la carpeta `src` dentro del proyecto (respeta el paquete `edu.proyecto`).
3. (Opcional) Crea las carpetas `data/input` y `data/output` en la raíz del proyecto.
4. Ejecuta `GenerateInfoFiles` (Run As → Java Application).  
   Verás `OK: Archivos de entrada generados...` y se crearán `sellers.csv`, `products.csv`, `sales.csv` en `data/input`.
5. Ejecuta `Main` (Run As → Java Application).  
   Verás `OK: Reportes generados...` y se crearán:
   - `report_sales_by_seller.csv`
   - `report_products_by_quantity.csv`
   - `resumen.txt`
   en `data/output`.

## Formatos de archivo
- **sellers.csv**: `seller_id,seller_name`
- **products.csv**: `product_id,product_name,category,unit_price`
- **sales.csv**: `sale_id,seller_id,product_id,quantity,date` (fecha ISO `yyyy-MM-dd`)

## Reportes generados
- **report_sales_by_seller.csv** (ordenado por ingresos desc):  
  `seller_id,seller_name,total_units,total_revenue,distinct_products,top_product_id,top_product_units`
- **report_products_by_quantity.csv** (ordenado por unidades desc):  
  `product_id,product_name,total_units,total_revenue,distinct_sellers,top_seller_id,top_seller_units`

## Manejo de errores
- Ambos programas capturan excepciones y muestran `ERROR: ...` en caso de fallos (rutas, CSV inválido, etc.).

## Entrega
- Sube el repositorio con:
  - `src/edu/proyecto/GenerateInfoFiles.java`
  - `src/edu/proyecto/Main.java`
  - carpeta `data/` (opcionalmente con ejemplos generados)
  - `conslusion.txt` (plantilla incluida a continuación)
  - `README.md`


## Usando el generador de un compañero
1. Ejecuta el generador (p. ej. del repo `sales-data-generator`) y copia sus CSV al directorio `data/input/` de este proyecto.
2. Este proyecto detecta automáticamente archivos llamados **sellers.csv / vendedores.csv**, **products.csv / productos.csv**, **sales.csv / ventas.csv**.
3. También mapea cabeceras alternativas en español/inglés (id/nombre/precio/fecha/cantidad).

Si tus archivos tienen otros nombres de columnas, ajusta las listas en `indexOf(...)` dentro de `Main.java`.
