FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

EXPOSE 8081

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
