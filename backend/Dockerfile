# Build: compile and package the jar (tests run in CI, they need Docker themselves)
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -q dependency:go-offline
COPY src src
RUN ./mvnw -q package -DskipTests

# Run: only the JRE and the jar
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/library-app-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
