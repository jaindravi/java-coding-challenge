# Crewmeister Test Assignment - Foreign Exchange Rate Service

## Overview
This Spring Boot application provides RESTful APIs for fetching foreign exchange rates, converting currencies to EUR, and retrieving available currencies. Exchange rates are fetched from an external API and persisted to a local database on application startup.

### Features
- get a list of all available currencies
- get all EUR-FX exchange rates at all available dates as a collection
- get the EUR-FX exchange rate at particular day
- get a foreign exchange amount for a given currencyDto converted to EUR on a particular day

## Tech Stack
- Java 17 
- Spring Boot 3 
- H2 In-Memory Database 
- JUnit 5 
- OpenAPI/Swagger 
- Lombok

## 🔄 How This Service Works

### 1. Application Startup
- On startup, the application triggers a bootstrap loader that fetches:
 - All available currencies from Bundesbank API (Metadata).
 - Historical exchange rates from the Bundesbank API (data).
- These are stored in an **H2 in-memory database**.

### 2. CurrencyAndRateScheduler
- A scheduled job handles two main tasks:

####  `loadInitialData()`
- Loads all available currencies and historical exchange rates.
- Uses `@Retryable` to retry on transient failures (e.g., network/API issues).
- Saves data asynchronously in batches using multithreading.

####  `fetchTodayExchangeRate()`
- Runs **daily at 5 AM**.
- Assumes Bundesbank API has updated rates for the current day.
- Fetches only **today’s exchange rate** and persists it.

---

### 3. API Endpoints

| Endpoint                                                           | Method | Description                                                                               |
|--------------------------------------------------------------------|--------|-------------------------------------------------------------------------------------------|
| `/api/currencies`                                                  | GET    | Get all available currencies                                                              |
| `/api/exchangeRates?currencyCode=XXX&date=YYYY-MM-DD`              | GET    | Get exchange rates for a currency. If `date` is not provided, returns all available rates |
| `/api/convertToEUR?currencyCode=XXX&amount=123.45&date=YYYY-MM-DD` | GET    | Converts the given amount to EUR for a specific date                                      |

---

### 4. Services & Data Flow

```plaintext
+-----------------------------+
| ExchangeRateBootstrapLoader|
+-------------+--------------+
              |
              |  fetches data from
              v
     +---------------------+        +------------------------+
     | RestClientUtil      |        | Bundesbank API         |
     +---------------------+        +------------------------+
              |
              v
+-----------------------------+
| ExchangeRateService         |
|  - getExchangeRates()       |
|  - getAmountInEUR()         |
+-----------------------------+
              |
              v
       +-------------+
       | H2 Database |
       +-------------+

+-----------------------------+
| CurrencyService             |
|  - getAllCurrencies()       |
+-----------------------------+
              |
              v
       +-------------+
       | H2 Database |
       +-------------+
```

>  All services read/write to the H2 database, making the app lightweight and ideal for demos or testing environments.

### Bundesbank API 
- public service provided by the German central bank
[Bundesbank API Documentation](https://api.statistiken.bundesbank.de/doc/index.html?urls.primaryName=English+REST+API+Documentation)

## Setup
#### Requirements
- Java 11 (will run with OpenSDK 15 as well)
- Maven 3.x

#### Project
The project was generated through the Spring initializer for Java
 11 with dev tools and Spring Web as dependencies. In order to build and 
 run it, you just need to click the green arrow in the Application class in your Intellij 
 CE IDE or run the following command from your project root und Linux or ios. 

````shell script
$ mvn spring-boot:run
````
After running, the project, switch to your browser and hit http://localhost:8080/api/currencies. You should see list of currencies

### Access Swagger UI for API testing:
[test-with-Swagger-ui](http://localhost:8080/swagger-ui/index.html)

### Testing API Endpoints

#### 1.  GET /api/currencies
   Description: Returns a list of all supported currencies.

```
Response:

[
  {
    "code": "USD",
    "name": "US Dollar"
  },
  {
    "code": "JPY",
    "name": "Japanese Yen"
  }
]
```
#### 2. GET /api/exchangeRates
Description: Retrieves the EUR exchange rates for a specific currency on a given date or all available dates.

#### Query Parameters

| Param          | Type   | Required | Description                                    |
|----------------|--------|----------|------------------------------------------------|
| `currencyCode` | String | Yes      | The 3-letter currency code (e.g., USD)         |
| `date`         | String | No       | Specific date (yyyy-MM-dd) for historical rate |


```
Response:

{
  "currencyCode": "USD",
  "exchangeRates": [
    {
      "date": "2024-04-15",
      "exchangeRate": 1.08
    },
    {
      "date": "2024-04-14",
      "exchangeRate": 1.07
    }
  ]
}
```

#### 3. GET /api/convertToEUR
Description: Converts a given amount in a foreign currency to its EUR value for a specific date.

#### Query Parameters

| Param          | Type       | Required | Description                                    |
|----------------|------------|----------|------------------------------------------------|
| `currencyCode` | String     | Yes      | The 3-letter currency code (e.g., USD)         |
| `date`         | String     | Yes      | Specific date (yyyy-MM-dd) for historical rate |
| `amount`       | BigDecimal | Yes      | Amount in foreign currency                     |


```
Response:

{
  "amount": 850,
  "exchangeRate": 1.18,
  "currencyCode": "EUR",
  "date": "2025-04-15"
}

```


### Future Enhancements

- **Replace H2 DB with MongoDB/PostgreSQL**  
  Use a persistent and production-grade database to retain data beyond app restarts and support scaling.


- **Use Redis for Caching**  
  Cache:
    - Frequently requested exchange rates (especially for the current day)
    - Currency lists to reduce DB hits and improve performance


- **Add Integration Tests using Testcontainers**  
  Ensure end-to-end flow validation with actual DBs like PostgreSQL, using containers for reliable testing.


- **Implement Retry with Exponential Backoff and Circuit Breaker**  
  Enhance the robustness of API calls to the Bundesbank by using libraries like Resilience4j or Spring Retry with backoff strategies.

