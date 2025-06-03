# ToggleFox Makefile
# Comprehensive build, test, and deployment automation

.PHONY: help build test clean docker run stop logs deploy dev docs

# Default target
.DEFAULT_GOAL := help

# Variables
APP_NAME := togglefox
VERSION := $(shell mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
DOCKER_IMAGE := $(APP_NAME):$(VERSION)
DOCKER_LATEST := $(APP_NAME):latest
COMPOSE_FILE := docker-compose.yml
MAVEN_OPTS := -Dmaven.repo.local=.m2/repository

# Colors for output
RED := \033[0;31m
GREEN := \033[0;32m
YELLOW := \033[0;33m
BLUE := \033[0;34m
MAGENTA := \033[0;35m
CYAN := \033[0;36m
WHITE := \033[0;37m
RESET := \033[0m

##@ Help
help: ## Display this help message
	@echo "$(CYAN)togglefox - Feature Flag Management System$(RESET)"
	@echo "$(CYAN)=============================================$(RESET)"
	@awk 'BEGIN {FS = ":.*##"; printf "\n$(BLUE)Usage:$(RESET)\n  make $(CYAN)<target>$(RESET)\n"} /^[a-zA-Z_0-9-]+:.*?##/ { printf "  $(CYAN)%-15s$(RESET) %s\n", $$1, $$2 } /^##@/ { printf "\n$(MAGENTA)%s$(RESET)\n", substr($$0, 5) } ' $(MAKEFILE_LIST)

##@ Development
dev: ## Start development environment with hot reload
	@echo "$(GREEN)Starting development environment...$(RESET)"
	@$(MAKE) deps-up
	@echo "$(YELLOW)Waiting for dependencies to be ready...$(RESET)"
	@sleep 10
	@cd togglefox-service/togglefox-container && \
		mvn spring-boot:run -Dspring-boot.run.profiles=dev $(MAVEN_OPTS)

dev-debug: ## Start development environment with debug enabled
	@echo "$(GREEN)Starting development environment with debug...$(RESET)"
	@$(MAKE) deps-up
	@echo "$(YELLOW)Waiting for dependencies to be ready...$(RESET)"
	@sleep 10
	@cd togglefox-service/togglefox-container && \
		mvn spring-boot:run -Dspring-boot.run.profiles=dev \
		-Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005" \
		$(MAVEN_OPTS)

deps-up: ## Start only dependencies (PostgreSQL, Redis)
	@echo "$(GREEN)Starting dependencies...$(RESET)"
	@docker compose -f $(COMPOSE_FILE) up -d postgres redis

deps-down: ## Stop dependencies
	@echo "$(YELLOW)Stopping dependencies...$(RESET)"
	@docker compose -f $(COMPOSE_FILE) down postgres redis

##@ Build & Test
clean: ## Clean build artifacts
	@echo "$(YELLOW)Cleaning build artifacts...$(RESET)"
	@mvn clean $(MAVEN_OPTS)
	@docker system prune -f
	@docker volume prune -f

compile: ## Compile the project
	@echo "$(GREEN)Compiling project...$(RESET)"
	@mvn compile $(MAVEN_OPTS)

build: clean ## Build the entire project
	@echo "$(GREEN)Building project...$(RESET)"
	@mvn package -DskipTests $(MAVEN_OPTS)
	@echo "$(GREEN)✓ Build completed successfully$(RESET)"

build-with-tests: clean ## Build project with all tests
	@echo "$(GREEN)Building project with tests...$(RESET)"
	@mvn package $(MAVEN_OPTS)
	@echo "$(GREEN)✓ Build with tests completed successfully$(RESET)"

##@ Testing
test: ## Run unit tests
	@echo "$(GREEN)Running unit tests...$(RESET)"
	@mvn test $(MAVEN_OPTS)
	@echo "$(GREEN)✓ Unit tests completed$(RESET)"

test-integration: ## Run integration tests
	@echo "$(GREEN)Running integration tests...$(RESET)"
	@mvn verify -Pfailsafe $(MAVEN_OPTS)
	@echo "$(GREEN)✓ Integration tests completed$(RESET)"

test-architecture: ## Run architecture tests
	@echo "$(GREEN)Running architecture tests...$(RESET)"
	@mvn test -Dtest="*ArchitectureTest" $(MAVEN_OPTS)
	@echo "$(GREEN)✓ Architecture tests completed$(RESET)"

test-performance: ## Run performance tests
	@echo "$(GREEN)Running performance tests...$(RESET)"
	@mvn test -Dperformance.tests.enabled=true $(MAVEN_OPTS)
	@echo "$(GREEN)✓ Performance tests completed$(RESET)"

test-all: ## Run all tests (unit, integration, architecture)
	@echo "$(GREEN)Running all tests...$(RESET)"
	@$(MAKE) test
	@$(MAKE) test-integration
	@$(MAKE) test-architecture
	@echo "$(GREEN)✓ All tests completed successfully$(RESET)"

coverage: ## Generate test coverage report
	@echo "$(GREEN)Generating coverage report...$(RESET)"
	@mvn jacoco:prepare-agent test jacoco:report $(MAVEN_OPTS)
	@echo "$(GREEN)✓ Coverage report generated at target/site/jacoco/index.html$(RESET)"

##@ Docker
docker-build: build ## Build Docker image
	@echo "$(GREEN)Building Docker image...$(RESET)"
	@docker build -f infrastructure/Dockerfile -t $(DOCKER_IMAGE) -t $(DOCKER_LATEST) .
	@echo "$(GREEN)✓ Docker image built: $(DOCKER_IMAGE)$(RESET)"

docker-run: ## Run application in Docker
	@echo "$(GREEN)Running application in Docker...$(RESET)"
	@docker compose -f $(COMPOSE_FILE) up -d
	@echo "$(GREEN)✓ Application started$(RESET)"
	@echo "$(CYAN)API available at: http://localhost:8080$(RESET)"
	@echo "$(CYAN)Swagger UI: http://localhost:8080/swagger-ui.html$(RESET)"

docker-stop: ## Stop Docker containers
	@echo "$(YELLOW)Stopping Docker containers...$(RESET)"
	@docker compose -f $(COMPOSE_FILE) down

docker-logs: ## Show Docker logs
	@docker compose -f $(COMPOSE_FILE) logs -f togglefox

docker-clean: ## Clean Docker images and containers
	@echo "$(YELLOW)Cleaning Docker resources...$(RESET)"
	@docker compose -f $(COMPOSE_FILE) down -v --rmi all --remove-orphans
	@docker system prune -af

##@ Quick Actions
run: docker-run ## Quick start - build and run everything

stop: docker-stop ## Quick stop - stop all services

restart: stop run ## Restart all services

logs: ## Show application logs
	@$(MAKE) docker-logs

health: ## Check application health
	@echo "$(GREEN)Checking application health...$(RESET)"
	@curl -s http://localhost:8080/actuator/health | jq . || echo "$(RED)Application not responding$(RESET)"

##@ API Testing
api-test: ## Run basic API tests
	@echo "$(GREEN)Testing API endpoints...$(RESET)"
	@chmod +x scripts/api-test.sh
	@./scripts/api-test.sh

create-sample-flag: ## Create a sample feature flag
	@echo "$(GREEN)Creating sample feature flag...$(RESET)"
	@curl -X POST http://localhost:8080/api/v1/flags \
		-H "Content-Type: application/json" \
		-d '{ \
			"name": "sample-feature", \
			"description": "Sample feature flag for testing", \
			"environment": "development", \
			"strategyType": "PERCENTAGE", \
			"strategyConfig": {"percentage": 50} \
		}' | jq .

evaluate-sample: ## Evaluate sample feature flag
	@echo "$(GREEN)Evaluating sample feature flag...$(RESET)"
	@curl -s "http://localhost:8080/api/v1/evaluate/sample-feature?environment=development&userId=testuser" | jq .

##@ Database
db-migrate: ## Run database migrations
	@echo "$(GREEN)Running database migrations...$(RESET)"
	@cd togglefox-service/togglefox-container && \
		mvn flyway:migrate $(MAVEN_OPTS)

db-reset: ## Reset database (WARNING: destroys all data)
	@echo "$(RED)WARNING: This will destroy all data!$(RESET)"
	@read -p "Are you sure? [y/N] " confirm && [ "$confirm" = "y" ] || exit 1
	@docker compose -f $(COMPOSE_FILE) down -v
	@docker compose -f $(COMPOSE_FILE) up -d postgres redis
	@echo "$(GREEN)✓ Database reset completed$(RESET)"

db-console: ## Open database console
	@echo "$(GREEN)Opening database console...$(RESET)"
	@docker compose -f $(COMPOSE_FILE) exec postgres psql -U togglefox -d togglefox

##@ Documentation
docs: ## Generate project documentation
	@echo "$(GREEN)Generating documentation...$(RESET)"
	@mvn javadoc:aggregate $(MAVEN_OPTS)
	@echo "$(GREEN)✓ Documentation generated at target/site/apidocs/index.html$(RESET)"

docs-serve: ## Serve documentation locally
	@echo "$(GREEN)Serving documentation at http://localhost:8081$(RESET)"
	@cd target/site && python3 -m http.server 8081

api-docs: ## Open API documentation
	@echo "$(GREEN)Opening API documentation...$(RESET)"
	@open http://localhost:8080/swagger-ui.html || xdg-open http://localhost:8080/swagger-ui.html

##@ Code Quality
lint: ## Run code linting
	@echo "$(GREEN)Running code linting...$(RESET)"
	@mvn checkstyle:check $(MAVEN_OPTS)

format: ## Format code
	@echo "$(GREEN)Formatting code...$(RESET)"
	@mvn fmt:format $(MAVEN_OPTS)

security-scan: ## Run security scan
	@echo "$(GREEN)Running security scan...$(RESET)"
	@mvn org.owasp:dependency-check-maven:check $(MAVEN_OPTS)

quality-gate: ## Run all quality checks
	@echo "$(GREEN)Running quality gate...$(RESET)"
	@$(MAKE) lint
	@$(MAKE) test-all
	@$(MAKE) security-scan
	@echo "$(GREEN)✓ Quality gate passed$(RESET)"

##@ Deployment
package: build ## Package application for deployment
	@echo "$(GREEN)Packaging application...$(RESET)"
	@mkdir -p dist
	@cp togglefox-service/togglefox-container/target/togglefox-container-*.jar dist/
	@cp infrastructure/docker-compose.yml dist/
	@cp -r infrastructure dist/
	@tar -czf dist/togglefox-$(VERSION).tar.gz -C dist .
	@echo "$(GREEN)✓ Package created: dist/togglefox-$(VERSION).tar.gz$(RESET)"

deploy-staging: docker-build ## Deploy to staging environment
	@echo "$(GREEN)Deploying to staging...$(RESET)"
	@docker tag $(DOCKER_IMAGE) $(APP_NAME):staging
	@echo "$(GREEN)✓ Staging deployment ready$(RESET)"

deploy-prod: docker-build ## Deploy to production environment
	@echo "$(GREEN)Deploying to production...$(RESET)"
	@docker tag $(DOCKER_IMAGE) $(APP_NAME):production
	@echo "$(GREEN)✓ Production deployment ready$(RESET)"

##@ Monitoring
monitor: ## Show application metrics
	@echo "$(GREEN)Application Metrics:$(RESET)"
	@curl -s http://localhost:8080/actuator/metrics | jq .
	@echo "\n$(GREEN)Health Status:$(RESET)"
	@curl -s http://localhost:8080/actuator/health | jq .

metrics: ## Show detailed metrics
	@echo "$(GREEN)JVM Metrics:$(RESET)"
	@curl -s http://localhost:8080/actuator/metrics/jvm.memory.used | jq .
	@echo "\n$(GREEN)HTTP Metrics:$(RESET)"
	@curl -s http://localhost:8080/actuator/metrics/http.server.requests | jq .

##@ Utilities
install-tools: ## Install required development tools
	@echo "$(GREEN)Installing development tools...$(RESET)"
	@command -v docker >/dev/null 2>&1 || { echo "$(RED)Docker is required but not installed$(RESET)"; exit 1; }
	@command -v docker >/dev/null 2>&1 && docker compose version >/dev/null 2>&1 || { echo "$(RED)Docker Compose is required but not installed$(RESET)"; exit 1; }
	@command -v mvn >/dev/null 2>&1 || { echo "$(RED)Maven is required but not installed$(RESET)"; exit 1; }
	@command -v java >/dev/null 2>&1 || { echo "$(RED)Java 17+ is required but not installed$(RESET)"; exit 1; }
	@command -v jq >/dev/null 2>&1 || { echo "$(YELLOW)jq not found, installing...$(RESET)"; sudo apt-get install -y jq || brew install jq; }
	@command -v curl >/dev/null 2>&1 || { echo "$(RED)curl is required but not installed$(RESET)"; exit 1; }
	@echo "$(GREEN)✓ All tools are available$(RESET)"

version: ## Show version information
	@echo "$(CYAN)togglefox Version Information$(RESET)"
	@echo "$(CYAN)=============================$(RESET)"
	@echo "Application Version: $(GREEN)$(VERSION)$(RESET)"
	@echo "Java Version: $(GREEN)$(shell java -version 2>&1 | head -n 1)$(RESET)"
	@echo "Maven Version: $(GREEN)$(shell mvn -version | head -n 1)$(RESET)"
	@echo "Docker Version: $(GREEN)$(shell docker --version)$(RESET)"

status: ## Show service status
	@echo "$(CYAN)Service Status$(RESET)"
	@echo "$(CYAN)==============$(RESET)"
	@docker compose -f $(COMPOSE_FILE) ps

##@ Profiles
profile-dev: ## Run with development profile
	@echo "$(GREEN)Starting with development profile...$(RESET)"
	@SPRING_PROFILES_ACTIVE=dev $(MAKE) run

profile-test: ## Run with test profile
	@echo "$(GREEN)Starting with test profile...$(RESET)"
	@SPRING_PROFILES_ACTIVE=test $(MAKE) run

profile-prod: ## Run with production profile
	@echo "$(GREEN)Starting with production profile...$(RESET)"
	@SPRING_PROFILES_ACTIVE=prod $(MAKE) run

##@ Backup & Restore
backup: ## Backup database
	@echo "$(GREEN)Creating database backup...$(RESET)"
	@mkdir -p backups
	@docker compose -f $(COMPOSE_FILE) exec -T postgres pg_dump -U togglefox togglefox > backups/backup-$(shell date +%Y%m%d-%H%M%S).sql
	@echo "$(GREEN)✓ Database backup created in backups/$(RESET)"

restore: ## Restore database from backup (specify BACKUP_FILE=filename)
	@echo "$(GREEN)Restoring database from backup...$(RESET)"
	@test -n "$(BACKUP_FILE)" || { echo "$(RED)Please specify BACKUP_FILE=filename$(RESET)"; exit 1; }
	@docker compose -f $(COMPOSE_FILE) exec -T postgres psql -U togglefox -d togglefox < backups/$(BACKUP_FILE)
	@echo "$(GREEN)✓ Database restored from $(BACKUP_FILE)$(RESET)"

##@ Troubleshooting
debug: ## Debug common issues
	@echo "$(CYAN)togglefox Debug Information$(RESET)"
	@echo "$(CYAN)============================$(RESET)"
	@echo "$(GREEN)Checking port availability...$(RESET)"
	@netstat -an | grep :8080 | head -5 || echo "Port 8080 is available"
	@echo "$(GREEN)Checking Docker containers...$(RESET)"
	@docker compose -f $(COMPOSE_FILE) ps
	@echo "$(GREEN)Checking application logs...$(RESET)"
	@docker compose -f $(COMPOSE_FILE) logs --tail=10 togglefox 2>/dev/null || echo "Application not running"

tail-logs: ## Tail application logs
	@docker compose -f $(COMPOSE_FILE) logs -f

watch-health: ## Watch health status continuously
	@watch -n 2 'curl -s http://localhost:8080/actuator/health | jq .'

##@ Quick Recipes
quick-start: install-tools build run ## Complete setup from scratch
	@echo "$(GREEN)🚀 togglefox is ready!$(RESET)"
	@echo "$(CYAN)API: http://localhost:8080$(RESET)"
	@echo "$(CYAN)Docs: http://localhost:8080/swagger-ui.html$(RESET)"

demo: quick-start create-sample-flag evaluate-sample ## Full demo setup

ci: clean build-with-tests docker-build ## Continuous integration pipeline
	@echo "$(GREEN)✓ CI pipeline completed successfully$(RESET)"

cd: ci deploy-staging ## Continuous deployment pipeline
	@echo "$(GREEN)✓ CD pipeline completed successfully$(RESET)"