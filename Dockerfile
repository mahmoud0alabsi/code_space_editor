# ----------- Build Stage ------------
FROM maven:3.9.9-eclipse-temurin-17-focal AS build

WORKDIR /app

# Copy dependencies and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# ----------- Run Stage --------------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Add Docker client
RUN apt-get update && apt-get install -y docker.io

# Expose Spring Boot default port
EXPOSE 8080

# Run Spring Boot normally
CMD ["java", "-jar", "app.jar"]
