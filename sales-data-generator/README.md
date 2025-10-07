💼 Sales Project (Java)

A Java-based application for automatically generating, processing, and reporting sales data.
The project demonstrates file handling, data aggregation, and report generation using core Java features only — without external libraries.

🧩 Key Requirement

There are only two classes with a main method — GenerateInfoFiles and Main.
No user input is required.

🗂️ Project Structure
SalesProjectJava/
 ├─ src/
 │   └─ edu/proyecto/
 │       ├─ GenerateInfoFiles.java
 │       └─ Main.java
 ├─ data/
 │   ├─ input/   (created when generating data)
 │   └─ output/  (created when generating reports)
 └─ README.md

⚙️ Steps in Eclipse EE

File → New → Java Project, name it: SalesProjectJava.

Copy the src folder into the project (keep the edu.proyecto package structure).

(Optional) Create the folders data/input and data/output at the project root.

Run GenerateInfoFiles → Run As → Java Application.
You’ll see:

OK: Input files generated...


The following files will be created in data/input/:

sellers.csv

products.csv

sales.csv

Run Main → Run As → Java Application.
You’ll see:

OK: Reports generated...


The following files will be created in data/output/:

report_sales_by_seller.csv

report_products_by_quantity.csv

summary.txt

📄 File Formats

sellers.csv: seller_id, seller_name

products.csv: product_id, product_name, category, unit_price

sales.csv: sale_id, seller_id, product_id, quantity, date (ISO format yyyy-MM-dd)

📊 Generated Reports

report_sales_by_seller.csv (sorted by revenue, descending):

seller_id, seller_name, total_units, total_revenue, distinct_products, top_product_id, top_product_units


report_products_by_quantity.csv (sorted by units sold, descending):

product_id, product_name, total_units, total_revenue, distinct_sellers, top_seller_id, top_seller_units

⚠️ Error Handling

Both programs handle exceptions and display messages such as:

ERROR: ...


in case of issues (invalid paths, malformed CSV, etc.).

📦 Submission

Include the following in your repository:

src/edu/proyecto/GenerateInfoFiles.java
src/edu/proyecto/Main.java
data/                  (optionally with generated examples)
conclusion.txt         (template included below)
README.md

🤝 Using a Teammate’s Generator

Run your teammate’s generator (e.g., from the sales-data-generator repo) and copy the generated CSV files into this project’s data/input/ directory.

The project automatically detects files named sellers.csv / vendedores.csv, products.csv / productos.csv, and sales.csv / ventas.csv.

It also maps alternative headers in both Spanish and English (id, name, price, date, quantity).

If your files have different column names, adjust the lists in the indexOf(...) methods inside Main.java.
