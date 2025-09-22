FROM gradle:8.13-jdk21 AS build
WORKDIR /app

# Cache dependencies
COPY build.gradle settings.gradle ./
COPY gradle gradle
RUN gradle dependencies --no-daemon || true

# Copy source
COPY . .

# Build the app (skip tests for faster builds)
RUN gradle bootJar --no-daemon -x test

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY auth-service-1.0.0.jar /app/auth-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseZGC", "-XX:+HeapDumpOnOutOfMemoryError", "-XX:MaxRAMPercentage=75.0", "-jar", "auth-service.jar"]