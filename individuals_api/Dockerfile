FROM eclipse-temurin:24-jdk AS builder

WORKDIR /app

COPY . .
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

RUN ./gradlew dependencies --no-daemon
RUN ./gradlew openApiGenerate --no-daemon
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:24-jdk

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar individuals_api.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "individuals_api.jar"]