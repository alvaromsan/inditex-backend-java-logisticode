# Inditex Backend - Java Logisticode

A Spring Boot backend project running in Docker containers, with PostgreSQL database and API documentation via Swagger/OpenAPI.

---

## Table of Contents

1. [Project Overview](#-project-overview)
2. [Setup & Installation (local)](#-setup--installation-local)
3. [Setup (remote)](#-setup-remote)
4. [API Documentation (Swagger / OpenAPI)](#-api-documentation-swagger--openapi)
5. [Running Tests (local)](#-running-tests-local)
6. [Documentation](#-documentation)

---

## 📝 Project Overview

This project provides backend services for the Inditex logistics system, implemented in **Java Spring Boot** and running in Docker containers:

- **App service**: Main Spring Boot application
- **Database service**: PostgreSQL/MySQL
- **Test runner**: Separate container to run Maven tests

### Features

- Explore all available REST endpoints
- Send test requests directly from the Swagger UI
- View request/response schemas automatically generated from your Spring Boot models

### Technologies used

- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **PostgreSQL(remote)/MySQL (local)**
- **Docker & Docker compose**
- **Render** (for deployment)
- **Swagger / OpenAPI** (for API documentation)

### Environment Variables

The application uses environment variables with hybrid implementation to configure database connection and server port. Example:

```bash
DATABASE_HOST=<your-db-host>:mysql
DATABASE_PORT=<your-db-port>:3306
DATABASE_NAME=<your-db-name>:inditex
DATABASE_USERNAME=<your-db-user>:root
DATABASE_PASSWORD=<your-db-password>:root
PORT=<your-server-port>:3000
```

### Notes

- Make sure your service is running and connected to the database before accessing Swagger UI.
- The Swagger UI path is configured in `application.properties`:

```properties
springdoc.swagger-ui.path=/api/docs
springdoc.api-docs.path=/api/docs/openapi.json
springdoc.swagger-ui.disable-swagger-default-url=true
```

---

## 💻 Setup & Installation (local)

### Prerequisites

- Docker (for containerized setup)

### Clone the Repository

```bash
git clone https://github.com/alvaromsan/inditex-backend-java-logisticode.git
cd inditex-backend-java-logisticode
```

### Build and run the containers

```bash
docker-compose up --build
```

### Access the application
[http://localhost:3000](http://localhost:3000)

### Stopping the containers

```bash
docker-compose down
```

---

## ☁️ Setup (remote)

The application is already deployed in remote using `Render` software. To access the application (note it might take some minutes to start the application as it is deployed on demand):
- `Health check endpoint`: https://inditex.onrender.com/health
- `Swagger API`: https://inditex.onrender.com/api/docs

---

## 📚 API Documentation (Swagger / OpenAPI)

This project uses **Springdoc OpenAPI** to generate Swagger API documentation.

### Accessing Swagger UI

- `Local`: http://localhost:3000/api/docs
- `Render`: https://inditex.onrender.com/api/docs

### Accessing OpenAPI JSON

- `Local`: http://localhost:3000/api/docs/openapi.json
- `Render`: https://inditex.onrender.com/api/docs/openapi.json

---

## 🧪 Running Tests (local)

This project uses a separate `test-runner` service in Docker Compose to run Maven tests. We use **Docker Compose profiles** to separate test execution from the main app startup.

```bash
docker-compose --profile tests run --rm test-runner
```

---

## 📓 Documentation

- [API Javadoc](docs/apidocs/index.html)
- [CURL commands - simple example](docs/commands.md)

---
