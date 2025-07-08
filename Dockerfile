# Stage 1: Build the application using Maven
# Use a base image for both Maven and Java
FROM maven:3.9.9 AS build

# Establishing the working directory
WORKDIR /app

# Copying the pom.xml file and the source code to the container
COPY pom.xml .
COPY src ./src

# Execute Maven to build the project
RUN mvn clean package -DskipTests

# --------------------------------------------------------
# Stage 2: Run the application
FROM openjdk:21

# Declaring build-time arguments (taken from pom.xml)
ARG ARTIFACT_ID=inditex
ARG VERSION=0.0.1-SNAPSHOT
ARG JAR_FILE=${ARTIFACT_ID}-${VERSION}.jar

# Establishing the working directory
WORKDIR /app

# Copying the JAR from the previous container
COPY --from=build /app/target/${JAR_FILE} app.jar

# Exposing the port
EXPOSE 3000

# Defining the entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"]