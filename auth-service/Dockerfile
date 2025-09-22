# Stage 1: Build the application
FROM gradle:8.13-jdk21 AS build
WORKDIR /app

# Copy and cache dependency files first to leverage Docker's layer caching
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon

# Copy the rest of the source code
COPY . .

# Build the Spring Boot application, creating the executable JAR
RUN gradle bootJar --no-daemon -x test

#-----------------------------------------------------------------------------------------------------------------------
# Stage 2: Create the final, lightweight image
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copy the JAR from the 'build' stage into the final image
COPY --from=build /app/build/libs/auth-service-1.0.0.jar ./auth-service.jar

# Expose the port the application listens on
EXPOSE 8081

# Define the command to run the application
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseZGC", "-XX:+HeapDumpOnOutOfMemoryError", "-XX:MaxRAMPercentage=75.0", "-jar", "auth-service.jar"]