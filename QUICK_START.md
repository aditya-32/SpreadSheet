# 🚀 Quick Start Guide

## One-Time Setup

```bash
# 1. Build the project
make build
```

## Start the Application

```bash
# 2. Start everything (PostgreSQL + Application)
make start
```

That's it! 🎉

**Access your application at:** http://localhost:8080

---

## Basic Commands

```bash
make build      # Build the project
make start      # Start everything
make stop       # Stop everything
make clean      # Clean build files
make help       # Show all commands
```

---

## Test the API

```bash
# Get all workbooks (sample data already loaded)
curl http://localhost:8080/api/workbooks

# Create a new workbook
curl -X POST http://localhost:8080/api/workbooks \
  -H "Content-Type: application/json" \
  -d '{"name": "My Workbook"}'

# Update a cell
curl -X PUT http://localhost:8080/api/sheets/1/cells \
  -H "Content-Type: application/json" \
  -d '{"rowIndex": 1, "columnIndex": 0, "value": "100"}'

# Add a formula
curl -X PUT http://localhost:8080/api/sheets/1/cells \
  -H "Content-Type: application/json" \
  -d '{"rowIndex": 2, "columnIndex": 0, "value": "=A1*2"}'
```

---

## What's Running?

- **PostgreSQL**: Port 5432 (with sample data)
- **Spring Boot API**: Port 8080
- **Sample Data**: 3 workbooks with formulas

---

## Stop Everything

```bash
make stop
```

This stops both the application and PostgreSQL.

---

## Troubleshooting

### Port already in use
```bash
# Stop everything first
make stop

# Then start again
make start
```

### Database issues
```bash
# Restart PostgreSQL
make stop
make postgres
```

### Build issues
```bash
# Clean and rebuild
make clean
make build
```

---

## Next Steps

- Read [README.md](README.md) for full documentation
- Check API examples in README
- Run tests: `mvn test`

---

**That's all you need!** 🎊

