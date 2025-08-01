# Stage 1: Build
FROM 11.11.7.195:5000/data-solution/gradle:8.11.1-jdk17 AS build

COPY . /app
WORKDIR /app

RUN chmod +x gradlew

RUN gradle bootJar --no-daemon

# Stage 2: Run
FROM 11.11.7.195:5000/data-solution/standalone-chrome:131.0-chromedriver-131.0
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
