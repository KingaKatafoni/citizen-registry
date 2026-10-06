# --- Stage 1: BUILD ---
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests

# --- Stage 2: RUNTIME ---
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/citizen-registry-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]