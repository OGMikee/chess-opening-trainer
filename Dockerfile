FROM maven:3.9-eclipse-temurin-18 AS build
WORKDIR /app
COPY backend/pom.xml .
COPY backend/mvnw .
COPY backend/mvnw.cmd .
COPY backend/.mvn .mvn
COPY backend/src src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:18-jre-alpine
WORKDIR /app
COPY --from=build /app/target/chess-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
