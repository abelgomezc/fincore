FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
COPY target/audit-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
