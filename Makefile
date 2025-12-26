.PHONY: help build start postgres stop clean

# Default target
help:
	@echo "================= Collaborative Spreadsheet ================="
	@echo ""
	@echo "  make build      - Build the project"
	@echo "  make postgres   - Start PostgreSQL database"
	@echo "  make start      - Start the application (with PostgreSQL)"
	@echo "  make stop       - Stop everything"
	@echo "  make clean      - Clean build artifacts"
	@echo ""
	@echo "Quick start: make build && make postgres && make start"
	@echo ""
	@echo "============================================================="

# Build project
build:
	@echo "📦 Building project..."
	mvn clean install

# Start PostgreSQL
postgres:
	@echo "🐘 Starting PostgreSQL..."
	cd docker && docker-compose up -d
	@echo "⏳ Waiting for database..."
	@sleep 5
	@echo "✅ PostgreSQL ready!"

# Start application
start: postgres
	@echo "🚀 Starting application..."
	@echo "📍 API: http://localhost:8080"
	@echo ""
	mvn spring-boot:run -Dspring-boot.run.profiles=docker

# Stop everything
stop:
	@echo "🛑 Stopping application..."
	@pkill -f "spring-boot:run" || true
	@echo "🛑 Stopping PostgreSQL..."
	cd docker && docker-compose down
	@echo "✅ Everything stopped"

# Clean
clean:
	@echo "🧹 Cleaning..."
	mvn clean
