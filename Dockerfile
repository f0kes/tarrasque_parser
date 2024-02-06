# Use a base image with Java and Kotlin support
FROM adoptopenjdk/openjdk17:jdk-17_0_2_7-alpine

# Set working directory in the container
WORKDIR /app

# Copy the application JAR file
COPY build/libs/dota-tarrasque-parser-1.0-SNAPSHOT-all.jar .

# Expose the port your app runs on
EXPOSE 8080

# Command to run your application
CMD ["java", "-jar", "dota-tarrasque-parser-1.0-SNAPSHOT-all.jar"]