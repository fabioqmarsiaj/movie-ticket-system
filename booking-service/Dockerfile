FROM eclipse-temurin:25-jdk-jammy

WORKDIR /app

# Copia o JAR já buildado localmente
COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "/app/app.jar"]