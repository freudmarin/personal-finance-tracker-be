# syntax=docker/dockerfile:1

# Build stage
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw mvnw
COPY pom.xml .
# Ensure the Maven wrapper has Unix line endings and execute permission in Linux containers
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw \
    && ./mvnw -q -B -e -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -q -B -DskipTests package

# Run stage
FROM eclipse-temurin:25-jre
VOLUME /tmp
ENV JAVA_OPTS=""
WORKDIR /app

COPY --from=build /workspace/target/personal-finance-tracker-be-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
