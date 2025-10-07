# 💼 Sales Project (Java)

A **Java-based application** for automatically generating, processing, and reporting sales data.  
The project demonstrates file handling, data aggregation, and report generation using core Java features only — **no external libraries required**.

---

## 🧩 Key Requirement

There are only **two classes** with a `main` method:  
`GenerateInfoFiles` and `Main`.  
No user input is required.

---

<img width="883" height="479" alt="image" src="https://github.com/user-attachments/assets/ca2977d5-1080-483a-9e3c-d32ffa6404c5" />





---

## ⚙️ Steps in Eclipse EE

1. **Create project:**  
   Go to `File → New → Java Project` and name it `SalesProjectJava`.

2. **Copy source code:**  
   Copy the `src` folder into the project (keep the `edu.proyecto` package structure).

3. **(Optional)** Create the folders `data/input` and `data/output` at the project root.

4. **Run the data generator:**  
   Execute `GenerateInfoFiles` → `Run As → Java Application`.

   You should see:

   OK: Input files generated...

   Files created in `data/input/`:
- `sellers.csv`
- `products.csv`
- `sales.csv`

5. **Run the main program:**  
Execute `Main` → `Run As → Java Application`.

You should see:

OK: Reports generated...

Files created in `data/output/`:
- `report_sales_by_seller.csv`
- `report_products_by_quantity.csv`
- `summary.txt`

---

## 📄 File Formats

| File | Columns |
|------|----------|
| **sellers.csv** | `seller_id, seller_name` |
| **products.csv** | `product_id, product_name, category, unit_price` |
| **sales.csv** | `sale_id, seller_id, product_id, quantity, date` (ISO format `yyyy-MM-dd`) |

---

## 📊 Generated Reports

### **report_sales_by_seller.csv**  
Sorted by **revenue (descending)**  

seller_id, seller_name, total_units, total_revenue, distinct_products, top_product_id, top_product_units


---

## ⚠️ Error Handling

Both programs handle exceptions and display:

ERROR: ...

in case of issues (invalid paths, malformed CSV, etc.).

---

## 📦 Submission

Include the following in your repository:

src/edu/proyecto/GenerateInfoFiles.java
src/edu/proyecto/Main.java
data/ (optionally with generated examples)
conclusion.txt (template included below)
README.md


---

## 🤝 Using a Teammate’s Generator

1. Run your teammate’s generator (e.g., from the `sales-data-generator` repo) and copy the generated CSV files into this project’s `data/input/` directory.  
2. This project automatically detects files named:
   - `sellers.csv` / `vendedores.csv`
   - `products.csv` / `productos.csv`
   - `sales.csv` / `ventas.csv`
3. It also maps alternative headers in both Spanish and English:
   - `id`, `name`, `price`, `date`, `quantity`

If your files have different column names, adjust the lists in the `indexOf(...)` methods inside `Main.java`.

---

## 👤 Author

Developed by **Julián Ortiz**  
*Politécnico Grancolombiano — Software Engineering Program*

