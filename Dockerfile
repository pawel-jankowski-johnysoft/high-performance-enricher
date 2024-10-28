FROM eclipse-temurin:21-jre

EXPOSE 80

COPY build/libs/high-performance-enricher-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
