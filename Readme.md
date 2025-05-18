# 🏢 Broker Backend - Stock Order Management API

This is a stock order management system designed for a brokerage firm. It supports managing customer stock orders with authentication, authorization, and concurrency-safe Redis locking for asset operations.

---

## 🔧 Tech Stack

| Layer            | Technology                        |
| ---------------- | --------------------------------- |
| Language         | Java 17                           |
| Framework        | Spring Boot 3.2+                  |
| Database         | H2 (in-memory)                    |
| Security         | Spring Security (JWT + Basic)     |
| Locking          | Redisson (Redis distributed lock) |
| API Docs         | Springdoc OpenAPI (Swagger UI)    |
| Testing          | JUnit 5, Mockito, WebMvcTest      |
| Containerization | Docker & Docker Compose           |

---

## 💡 Features

### Core Modules

* Create, list, and cancel stock orders (BUY / SELL)
* Asset reservation and management per customer
* Asset release on canceled SELL orders

### Bonus 1: Authentication & Authorization

* ✉️ JWT-based authentication for customers
* 🔐 Basic Auth for admin users (in-memory)
* ❌ Customers can only access their own data
* ✉️ `/api/authentication/login` endpoint to issue JWTs

### Bonus 2: Order Matching (ADMIN only)

* Match pending orders manually (update asset balances accordingly)
* Only admin can access this endpoint

### Concurrency

* Redis-based distributed locking for:

    * Reserving assets
    * Releasing assets
    * Creating/updating assets
    * Canceling orders

---

## 📂 Endpoints Summary

### 📅 Order Endpoints (`/api/orders`)

* `POST` Create new order
* `GET` List orders (with filters)
* `DELETE /{orderId}` Cancel pending order (only owner or admin)

### 💰 Asset Endpoints (`/api/assets`)

* `GET` List assets (admin can query any, user only own)

### 🔑 Auth (`/api/authentication`)

* `POST /login` Authenticate customer and get JWT

### 👥 Customer (`/api/customers`)

* `POST` Register new customer

---

## 🚀 Getting Started

### 📁 Prerequisites

* Java 17
* Maven 3+
* Docker & Docker Compose

### 📦 Run via Local Environment

```bash
# Clone project
$ git clone https://github.com/daidorian09/Broker-Firm-App.git
$ cd broker-firm-app

```

### 🐳 Start Redis with Docker Compose
* Make sure Docker is installed and running

```bash
docker-compose.yml:

version: "3.8"
services:
  redis:
    image: redis:7-alpine
    container_name: redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    restart: unless-stopped
    command: ["redis-server", "--appendonly", "yes"]

volumes:
  redis-data:
```
* Start it with:
```bash
docker-compose up -d redis
```

* Then run the application
```bash
# Run with Maven
$ ./mvnw clean package -DskipTests
$ ./mvnw spring-boot:run
```

### 📦 Run via Docker Compose

```bash
# Build the app jar
mvn clean package -DskipTests

# Run app + Redis
docker-compose up --build
```

> Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### 👷 Admin Credentials

```
Username: admin
Password: 1234
```

Use basic auth in Swagger or pass `Authorization: Basic YWRtaW46MTIzNA==`

### 🔑 JWT Usage (Customer)

* Call `/api/authentication/login` with email & password
* Use returned JWT as:

  ```
  Authorization: Bearer <token>
  ```

---

## 📅 Sample Users

```json
{
  "email": "john@example.com",
  "password": "123456"
}
```

---

## 🔋 Redis Configuration (via Docker)

* Redis runs on internal network as `redis:6379`
* Profile `docker` must be active:

  ```bash
  SPRING_PROFILES_ACTIVE=docker
  ```
* application-docker.properties:

  ```properties
  broker.config.redis.host=redis
  broker.config.redis.port=6379
  ```

---

## 💡 Testing

### ✅ Unit & Integration Tests
![Coverage](https://img.shields.io/badge/Coverage-99%25-brightgreen)
* Total test cases: 126 (unit + integration)
* Test coverage: **99%**
* 
```bash
mvn test
```

## 🚫 Permissions

* **Customer (JWT)**: Can only access their own orders & assets
* **Admin (Basic Auth)**: Can list, match, and cancel any order

---

## 🌐 OpenAPI & Swagger

* Integrated with Springdoc (`springdoc-openapi`)
* Automatically generates doc from annotations
* Supports `basicAuth` & `bearerAuth` schemes

---

## 🎉 Author

Developed by Yiğit At

---

## 🧠 Notes
* Authorization is implemented for **/api/assets [GET]**, **/api/orders [GET]** and **/api/orders/orderId [DELETE]** endpoints
* Docker support is not fully working, application is crashing during docker-compose up command reading redis config although **application-docker.properties** is existent in resources
* ⚠️ H2 does not fully support **OPTIMISTIC_READ** locking — Redis is used to guarantee concurrency control during BUY/SELL operations.
---

## ✨ Future Improvements

* Order matching engine (automatic)
* Fix docker-compose build
* Authorization on all endpoint
---
