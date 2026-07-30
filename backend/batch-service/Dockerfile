FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="Abel Gomez 2026"

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","/app.jar"]
