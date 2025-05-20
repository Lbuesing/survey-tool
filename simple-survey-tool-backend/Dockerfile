# Use OpenJDK base image
FROM openjdk:17-jdk-slim AS build

# Set working directory
WORKDIR /app

# Copy the application jar file into the image
COPY ./build/libs/*SNAPSHOT.jar app.jar

# Expose the port that the Spring Boot app will run on (8080)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
