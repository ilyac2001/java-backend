FROM gradle:8.7-jdk17 AS builder
WORKDIR /app

COPY . .

RUN gradle clean bootJar -Pversion=0.0.1-SNAPSHOT

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/build/libs/apigateway-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]