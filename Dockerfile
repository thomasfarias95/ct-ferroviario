# Estágio 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Passo estratégico: Copia apenas o pom.xml para baixar as dependências primeiro
COPY pom.xml .
RUN mvn dependency:go-offline


COPY src ./src
RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app


COPY --from=build /app/target/*.jar app.jar


EXPOSE 10000
ENTRYPOINT ["java", "-jar", "app.jar"]