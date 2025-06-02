

.PHONY: all build clean test run docker-build docker-run help

# Variables
APP_NAME := togglefox
DOCKER_IMAGE_NAME := com/togglefox/${APP_NAME}
DOCKER_IMAGE_TAG := latest
CONTAINER_NAME := ${APP_NAME}-container

# Default target
all: build

# Build the entire Maven project
build:
	@echo "Building Maven project..."
	mvn clean package -DskipTests

# Build the Maven project and run tests
build-with-tests:
	@echo "Building Maven project and running tests..."
	mvn clean package

# Clean Maven project
clean:
	@echo "Cleaning Maven project..."
	mvn clean

# Run tests for all modules
test:
	@echo "Running tests..."
	mvn test

# Run the Spring Boot application (container module)
run: build
	@echo "Running Spring Boot application..."
	java -jar togglefox-service/togglefox-container/target/togglefox-container-*.jar

# Build Docker image
docker-build:
	@echo "Building Docker image ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}..."
	docker build -t ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} -f infrastructure/Dockerfile .

# Run Docker container using docker-compose
docker-run:
	@echo "Running Docker containers with docker-compose..."
	docker-compose -f infrastructure/docker-compose.yml up

# Run Docker container with build using docker-compose
docker-up:
	@echo "Building and running Docker containers with docker-compose..."
	docker-compose -f infrastructure/docker-compose.yml up --build

# Stop Docker containers run by docker-compose
docker-stop:
	@echo "Stopping Docker containers..."
	docker-compose -f infrastructure/docker-compose.yml down

# Show help
help:
	@echo "Available commands:"
	@echo "  make build             - Build the Maven project (skips tests)"
	@echo "  make build-with-tests  - Build the Maven project and run tests"
	@echo "  make clean             - Clean the Maven project"
	@echo "  make test              - Run tests for all modules"
	@echo "  make run               - Build and run the Spring Boot application directly"
	@echo "  make docker-build      - Build the Docker image"
	@echo "  make docker-run        - Run Docker containers using docker-compose"
	@echo "  make docker-up         - Build (if needed) and run Docker containers with docker-compose"
	@echo "  make docker-stop       - Stop Docker containers run by docker-compose"