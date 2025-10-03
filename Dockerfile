# Build a minimal runtime image for the packaged Spring Boot jar
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Default to the packaged jar produced by Maven; Jenkins can override with --build-arg
ARG JAR_FILE=target/myspringapp-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
