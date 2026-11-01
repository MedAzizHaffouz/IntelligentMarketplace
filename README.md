# DataPulse Analytics

DataPulse Analytics is a Spring Boot web application for exploring customer, order, payment, and review data from the Brazilian Olist marketplace dataset. It provides an interactive dashboard, filtering by date and state, CSV report export, an H2 database console, and a lightweight analytics assistant.

## Project layout

The Maven application is located in the nested [`datapulse-analytics/`](./datapulse-analytics/) directory. This README intentionally lives at the workspace root.

```text
datapulse-analytics/
├── README.md                         # This file
└── datapulse-analytics/
    ├── pom.xml                       # Maven build definition
    └── src/
        ├── main/java/com/example/datapulse_analytics/
        │   ├── config/               # Spring Security configuration
        │   ├── controller/           # HTTP and REST endpoints
        │   ├── model/                # JPA entities
        │   ├── repository/           # Spring Data JPA repositories and queries
        │   └── service/              # CSV import and data-loading services
        ├── main/resources/
        │   ├── application.properties
        │   └── static/index.html      # Single-page dashboard UI
        └── test/                      # Spring context test
```

## Technology stack

- Java 17
- Spring Boot 3.2.12
- Spring MVC and Spring Security
- Spring Data JPA / Hibernate
- H2 in-memory database
- Apache Commons CSV 1.11.0
- Maven Wrapper (`mvnw` / `mvnw.cmd`)
- A static HTML dashboard using Tailwind CSS, Chart.js, html2canvas, and jsPDF from CDNs

## Main features

- KPI summary for customers, orders, revenue, and average review score
- Customer distribution by Brazilian state
- Monthly revenue trend
- Payment-method revenue distribution
- Date and state filters
- CSV report download
- Browser-based chat assistant with predefined analytics responses
- H2 console for inspecting the in-memory database

## Data flow

On startup, [`OlistDataImporter`](./datapulse-analytics/src/main/java/com/example/datapulse_analytics/service/OlistDataImporter.java) loads the Olist CSV files into H2 when the corresponding tables are empty. Controllers query the repositories, and the dashboard in [`index.html`](./datapulse-analytics/src/main/resources/static/index.html) calls the REST APIs.

The database is in memory, so imported data is lost when the application stops. Each new application run imports the CSV data again unless the process remains alive.

## Required dataset files

The importer expects these files in the configured data directory:

```text
olist_customers_dataset.csv
olist_orders_dataset.csv
olist_order_items_dataset.csv
olist_order_payments_dataset.csv
olist_order_reviews_dataset.csv
```

### Dataset source

The CSV files come from Kaggle's **Brazilian E-Commerce Public Dataset by Olist**:

- [Brazilian E-Commerce Public Dataset by Olist on Kaggle](https://www.kaggle.com/datasets/olistbr/brazilian-ecommerce)

The [`OlistDataImporter`](./datapulse-analytics/src/main/java/com/example/datapulse_analytics/service/OlistDataImporter.java) maps the dataset's customer, order, order-item, payment, and review files into the application's H2 entities at startup.

The default path is `C:\Users\azizh\Downloads\archive`. It is configured as `app.data-directory` in [`application.properties`](./datapulse-analytics/src/main/resources/application.properties). The importer currently also contains a matching hard-coded path, so update both locations if the dataset is moved.

## Run locally

From PowerShell:

```powershell
cd C:\datapulse-analytics\datapulse-analytics
.\mvnw.cmd spring-boot:run
```

Alternatively, build and run the packaged application:

```powershell
cd C:\datapulse-analytics\datapulse-analytics
.\mvnw.cmd clean package
java -jar .\target\datapulse-analytics-0.0.1-SNAPSHOT.jar
```

Open [http://localhost:8080/](http://localhost:8080/).

## REST API

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/` | Serves the dashboard |
| `GET` | `/api/dashboard/summary` | KPI summary; accepts `startDate`, `endDate`, and `state` |
| `GET` | `/api/dashboard/revenue/monthly` | Monthly revenue; accepts the same filters |
| `GET` | `/api/dashboard/payments/by-method` | Revenue grouped by payment method |
| `GET` | `/api/customers` | Lists customers |
| `GET` | `/api/customers/count` | Counts customers |
| `GET` | `/api/customers/stats/by-state` | Counts customers by state; accepts the dashboard filters |
| `GET` | `/api/customers/{customerId}` | Gets one customer |
| `GET` | `/api/reports/csv` | Downloads a filtered CSV report |
| `POST` | `/api/chat` | Returns a data-backed assistant response for `{ "message": "..." }` |
| `GET` | `/h2-console/` | Opens the H2 console |

Example filtered request:

```text
http://localhost:8080/api/dashboard/summary?startDate=2017-01-01&endDate=2017-12-31&state=SP
```

## H2 console

Use these values at [http://localhost:8080/h2-console/](http://localhost:8080/h2-console/):

```text
JDBC URL: jdbc:h2:mem:olistdb
User:     sa
Password:                 (empty)
```

## Test and build

```powershell
cd C:\datapulse-analytics\datapulse-analytics
.\mvnw.cmd test
```

The current test suite contains a Spring application-context smoke test. Because the application imports CSV data during startup, tests also require the dataset files to be available at the configured path.

## Configuration and operational notes

- The server listens on port `8080`.
- H2 is in-memory and configured with `spring.jpa.hibernate.ddl-auto=update`.
- All application routes are currently permitted by `SecurityConfig`; this is suitable for local development but should be tightened before production use.
- The dashboard loads third-party frontend libraries from CDNs, so offline use requires bundling those assets locally.
- The repository `.gitignore` excludes Maven build output, IDE metadata, and VS Code settings; dataset CSVs are not included in the project.

## Troubleshooting

If the page opens but shows a dashboard API error:

1. Confirm the Spring Boot process is running on port 8080.
2. Confirm all five CSV files exist in the configured data directory.
3. Check the application console for CSV parsing or database import errors.
4. Test the summary endpoint directly:

   ```powershell
   Invoke-WebRequest http://localhost:8080/api/dashboard/summary
   ```

5. If the application was rebuilt, stop the old process and start the newest packaged application or Maven run configuration.
