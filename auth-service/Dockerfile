FROM openjdk:21-jdk-slim

WORKDIR /app

COPY auth-service-1.0.0.jar /app/auth-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseZGC", "-XX:+HeapDumpOnOutOfMemoryError", "-XX:MaxRAMPercentage=75.0", "-jar", "auth-service.jar"]