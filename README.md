# Collaborative Spreadsheet Application

A production-ready Google Sheets-like application built with Spring Boot, featuring formula evaluation, cycle detection, concurrent user support, and auto-save functionality.

---

## 🚀 Quick Start

### Option 1: Run with H2 (In-Memory Database)

```bash
# Build and run
mvn clean install
mvn spring-boot:run

# Application runs at: http://localhost:8080
# H2 Console: http://localhost:8080/h2-console
```

### Option 2: Run with PostgreSQL (Docker)

```bash
# 1. Start PostgreSQL
cd docker
docker-compose up -d

# 2. Verify database is running
docker-compose ps
# Should show: spreadsheet-postgres (healthy), spreadsheet-adminer (up)

# 3. Run application with docker profile
cd ..
mvn spring-boot:run -Dspring-boot.run.profiles=docker

# Access:
# - API: http://localhost:8080
# - Adminer (DB UI): http://localhost:8081
```

---

## 🧪 Testing

```bash
# Run all tests (49 tests including concurrency)
mvn test

# Expected output:
# Tests run: 49, Failures: 0, Errors: 0, Skipped: 0
# BUILD SUCCESS

# Test specific component
mvn test -Dtest=ConcurrencyTest
mvn test -Dtest=FormulaEvaluatorTest
```

---

## 📡 API Contracts

### Base URL
```
http://localhost:8080/api
```

### 1. Workbook APIs

#### Create Workbook
```http
POST /workbooks
Content-Type: application/json

{
  "name": "My Spreadsheet",
  "sheetName": "Sheet1"
}

Response: 201 Created
{
  "success": true,
  "data": {
    "id": 1,
    "name": "My Spreadsheet",
    "sheets": [
      {
        "id": 1,
        "name": "Sheet1",
        "rowCount": 1000,
        "columnCount": 26
      }
    ]
  }
}
```

#### Get Workbook
```http
GET /workbooks/{id}

Response: 200 OK
```

#### List All Workbooks
```http
GET /workbooks

Response: 200 OK
```

#### Delete Workbook
```http
DELETE /workbooks/{id}

Response: 200 OK
```

---

### 2. Sheet APIs

#### Create Sheet
```http
POST /sheets/workbook/{workbookId}
Content-Type: application/json

{
  "name": "Budget",
  "rowCount": 1000,
  "columnCount": 26
}

Response: 201 Created
```

#### Get Sheet with Cells
```http
GET /sheets/{id}

Response: 200 OK
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Sheet1",
    "cells": [
      {
        "rowIndex": 1,
        "columnIndex": 0,
        "address": "A1",
        "rawValue": "100",
        "computedValue": "100"
      }
    ]
  }
}
```

---

### 3. Cell APIs

#### Update Single Cell
```http
PUT /sheets/{sheetId}/cells
Content-Type: application/json

{
  "rowIndex": 1,
  "columnIndex": 0,
  "value": "100"
}

Response: 200 OK
{
  "success": true,
  "data": {
    "id": 1,
    "rowIndex": 1,
    "columnIndex": 0,
    "address": "A1",
    "cellType": "NUMBER",
    "rawValue": "100",
    "computedValue": "100"
  }
}
```

#### Update Cell with Formula
```http
PUT /sheets/{sheetId}/cells
Content-Type: application/json

{
  "rowIndex": 3,
  "columnIndex": 0,
  "value": "=A1+A2"
}

Response: 200 OK
{
  "success": true,
  "data": {
    "address": "A3",
    "cellType": "FORMULA",
    "rawValue": "=A1+A2",
    "computedValue": "300"
  }
}
```

#### Batch Update Cells
```http
PUT /sheets/{sheetId}/cells/batch
Content-Type: application/json

{
  "cells": [
    {"rowIndex": 1, "columnIndex": 0, "value": "10"},
    {"rowIndex": 2, "columnIndex": 0, "value": "20"},
    {"rowIndex": 3, "columnIndex": 0, "value": "=SUM(A1:A2)"}
  ]
}

Response: 200 OK
{
  "success": true,
  "message": "Successfully updated 3 cells"
}
```

#### Get Specific Cell
```http
GET /sheets/{sheetId}/cells?rowIndex=1&columnIndex=0

Response: 200 OK
```

#### Get All Non-Empty Cells
```http
GET /sheets/{sheetId}/cells/all

Response: 200 OK
{
  "success": true,
  "data": [
    {"address": "A1", "value": "100"},
    {"address": "A2", "value": "200"},
    {"address": "A3", "value": "300"}
  ]
}
```

---

## 📊 Supported Formulas

### Arithmetic Operations
```
=A1+B1          Addition
=A1-B1          Subtraction
=A1*B1          Multiplication
=A1/B1          Division
=(A1+B1)*C1     Complex expressions
```

### Functions
```
=SUM(A1:A10)        Sum of range
=AVERAGE(A1:A10)    Average of range
=COUNT(A1:A10)      Count numeric cells
```

### Cell References
```
A1              Single cell
A1:A10          Column range
A1:E1           Row range
A1:C3           Rectangular range
```

---

## 🔒 Concurrency Handling

The application handles concurrent updates using:

1. **Optimistic Locking** - `@Version` annotation on entities
2. **Automatic Retry** - Exponential backoff (100ms → 200ms → 400ms)
3. **Verified** - 3 comprehensive concurrency tests

Multiple users can edit the same spreadsheet simultaneously without data corruption.

---

## 🗄️ Database Setup (Docker)

### PostgreSQL Configuration

The `docker/` directory contains:
- `schema.sql` - Database schema
- `data.sql` - Sample data (budget, sales examples)
- `docker-compose.yml` - PostgreSQL + Adminer setup

### Start Database
```bash
cd docker
docker-compose up -d
```

### Access Database
- **Adminer UI**: http://localhost:8081
  - System: PostgreSQL
  - Server: postgres
  - Username: spreadsheet_user
  - Password: spreadsheet_pass
  - Database: spreadsheet

- **psql CLI**:
  ```bash
  docker exec -it spreadsheet-postgres psql -U spreadsheet_user -d spreadsheet
  ```

### Stop Database
```bash
docker-compose down          # Stop but keep data
docker-compose down -v       # Stop and remove data
```

---

## 🔥 Example Usage

### Create a Budget Spreadsheet

```bash
# 1. Create workbook
curl -X POST http://localhost:8080/api/workbooks \
  -H "Content-Type: application/json" \
  -d '{"name": "Budget 2024", "sheetName": "January"}'

# Response: {"data": {"id": 1, "sheets": [{"id": 1}]}}

# 2. Add income
curl -X PUT http://localhost:8080/api/sheets/1/cells/batch \
  -H "Content-Type: application/json" \
  -d '{
    "cells": [
      {"rowIndex": 1, "columnIndex": 0, "value": "Income"},
      {"rowIndex": 2, "columnIndex": 0, "value": "Salary"},
      {"rowIndex": 2, "columnIndex": 1, "value": "5000"},
      {"rowIndex": 3, "columnIndex": 0, "value": "Freelance"},
      {"rowIndex": 3, "columnIndex": 1, "value": "2000"},
      {"rowIndex": 4, "columnIndex": 0, "value": "Total"},
      {"rowIndex": 4, "columnIndex": 1, "value": "=SUM(B2:B3)"}
    ]
  }'

# 3. View results
curl http://localhost:8080/api/sheets/1/cells/all
# Shows: B4 = 7000 (calculated from SUM formula)
```

---

## ⚠️ Error Handling

### Error Symbols
- `#DIV/0!` - Division by zero
- `#CYCLE!` - Circular dependency detected
- `#REF!` - Invalid cell reference
- `#ERROR!` - Formula parsing error
- `#VALUE!` - Invalid value type

### Example: Circular Dependency
```bash
# Create A1 = A2
curl -X PUT http://localhost:8080/api/sheets/1/cells \
  -d '{"rowIndex": 1, "columnIndex": 0, "value": "=A2"}'

# Try to create A2 = A1 (will fail)
curl -X PUT http://localhost:8080/api/sheets/1/cells \
  -d '{"rowIndex": 2, "columnIndex": 0, "value": "=A1"}'

# Response: {"success": false, "message": "#CYCLE! Circular dependency detected"}
```

---

## 🏗️ Architecture

### Technology Stack
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 21
- **Database**: H2 (dev) / PostgreSQL (production)
- **ORM**: Hibernate/JPA
- **Formula Engine**: exp4j
- **Testing**: JUnit 5, Mockito

### Key Components
1. **Formula Engine** - Parser, evaluator, cycle detection (DFS algorithm)
2. **Concurrency** - Optimistic locking with automatic retry
3. **Auto-save** - Event-driven with 3-second debouncing
4. **REST API** - Full CRUD operations with validation

### Design Patterns
- Layered Architecture (Controller → Service → Repository)
- Strategy Pattern (formula evaluation)
- Observer Pattern (auto-save events)
- Repository Pattern (data access)
- DTO Pattern (API separation)

---

## 📈 Performance

### Test Results (10 concurrent threads)
```
Total Tests: 49 (100% passing)
Concurrency Success: 100% (with retries)
Average Update: ~45ms
Max Update: ~180ms (with retries)
```

### Database
- **Storage**: Sparse (only non-empty cells stored)
- **Indexes**: 5 performance indexes
- **Connection Pool**: Configured with HikariCP

---

## 🐛 Troubleshooting

### Port Already in Use
```bash
# Change port in docker-compose.yml
ports:
  - "5433:5432"  # Use different host port

# Update application-docker.yml
url: jdbc:postgresql://localhost:5433/spreadsheet
```

### Tests Failing
```bash
# Clean and rebuild
mvn clean install

# Check Java version
java -version  # Should be 21
```

### Database Connection Issues
```bash
# Check Docker is running
docker-compose ps

# View logs
docker-compose logs postgres

# Restart
docker-compose restart
```

---

## 📚 Configuration

### application.yml (H2 - Development)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:spreadsheet
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
```

### application-docker.yml (PostgreSQL - Production)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/spreadsheet
    username: spreadsheet_user
    password: spreadsheet_pass
  jpa:
    hibernate:
      ddl-auto: validate
```

### Auto-save Configuration
```yaml
spreadsheet:
  autosave:
    enabled: true
    interval-ms: 3000    # Save every 3 seconds
    batch-size: 100      # Max cells per batch
```

---

## ✅ What's Included

- ✅ Full spreadsheet CRUD operations
- ✅ Formula evaluation (arithmetic, SUM, AVERAGE, COUNT)
- ✅ Cycle detection with DFS algorithm
- ✅ Concurrent user support (optimistic locking)
- ✅ Auto-save with debouncing
- ✅ Error handling with proper symbols
- ✅ 49 comprehensive tests
- ✅ Docker setup for PostgreSQL
- ✅ REST API with validation
- ✅ Production-ready code

---

## 📞 Quick Reference

```bash
# Development (H2)
mvn spring-boot:run

# Production (PostgreSQL)
cd docker && docker-compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=docker

# Testing
mvn test

# API Base URL
http://localhost:8080/api

# Database UI (PostgreSQL)
http://localhost:8081
```

---

## 📄 License

This project is for educational and demonstration purposes.
