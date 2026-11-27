# ETAPA 1: Compilar Coffee Tec con Maven
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests package

# ETAPA 2: Ejecutar Coffee Tec con Tomcat
FROM tomcat:10.1-jre17-temurin

COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/CoffeeTecBackend.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
