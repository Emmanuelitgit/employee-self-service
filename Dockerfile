# Stage 1: Build stage
FROM openjdk:19-jdk AS build
WORKDIR /app

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime stage
FROM openjdk:19-jdk
WORKDIR /app

EXPOSE 5000
VOLUME /tmp

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]