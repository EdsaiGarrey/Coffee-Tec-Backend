# ETAPA 1: Compilar Coffee Tec con Maven
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests package

# ETAPA 2: Ejecutar el WAR con GlassFish 7
FROM ghcr.io/eclipse-ee4j/glassfish:7.1.0

COPY --from=build /app/target/*.war /deploy/CoffeeTecBackend.war

EXPOSE 8080
