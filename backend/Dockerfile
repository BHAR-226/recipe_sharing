# Stage 1: Build the Spring Boot JAR
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Compile and package application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal Java Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Ensure uploads directory exists
RUN mkdir -p uploads/images

# Copy the compiled jar from build stage
COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
