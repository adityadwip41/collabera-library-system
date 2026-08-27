FROM maven:3.9.11-eclipse-temurin-17-alpine AS build
WORKDIR /workspace
COPY pom.xml ./
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S library && adduser -S library -G library
WORKDIR /app
COPY --from=build /workspace/target/library-system-*.jar app.jar
USER library
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
