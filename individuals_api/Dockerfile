FROM gradle:8.14.3-jdk24 AS builder

WORKDIR /app

COPY . .

RUN gradle clean build --no-daemon

FROM eclipse-temurin:24-jre-alpine-3.20

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar individuals_api.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "individuals_api.jar"]