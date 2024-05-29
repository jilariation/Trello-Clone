FROM gradle:jdk21-alpine as builder
WORKDIR /app
COPY . /app/.
RUN gradle clean build -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/*.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/*.jar"]