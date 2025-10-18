# ---- Stage 1: Build ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven configuration and source code
COPY pom.xml .
COPY src ./src

# Build the executable JAR (skip tests for faster builds)
RUN mvn clean package -DskipTests

# ---- Stage 2: Run ----
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy the JAR built in the previous stage
COPY --from=build /app/target/blog-application-0.0.1-SNAPSHOT.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
