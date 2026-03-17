# Estágio de Build
FROM eclipse-temurin:21-jdk-jammy as build
WORKDIR /app
COPY . .
# Dá permissão e executa o build
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Estágio de Execução
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]