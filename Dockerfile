FROM openjdk:21-jdk-slim

LABEL maintainer="Togglefox Team"
LABEL description="Togglefox Feature Flag Management System"

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create app directory
WORKDIR /app

# Copy the jar file
COPY togglefox-service/togglefox-container/target/togglefox-container-*.jar app.jar

# Create non-root user
RUN addgroup --system togglefox && adduser --system --group togglefox

# Create log directory and assign permissions
RUN mkdir -p /var/log/togglefox && chown -R togglefox:togglefox /var/log/togglefox

# Assign ownership of app directory
RUN chown -R togglefox:togglefox /app

# Switch to non-root user
USER togglefox

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
