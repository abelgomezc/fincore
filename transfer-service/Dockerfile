FROM maven:3.9-eclipse-temurin AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-jre
VOLUME /tmp
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java
-jar
/app.jar"]

