FROM openjdk:17-jdk-slim

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
# COPY gradle-8.4-bin.zip .

# Make gradlew executable
RUN chmod +x gradlew

# Copy source code
COPY src src

# Create images directory
RUN mkdir -p /app/images

# Build the application
RUN ./gradlew build -x test

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
CMD ["java", "-jar", "build/libs/bookstore-0.0.1-SNAPSHOT.jar"] 