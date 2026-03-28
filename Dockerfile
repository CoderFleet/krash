FROM eclipse-temurin:24-jdk as builder

WORKDIR /app

COPY gradle gradle
COPY gradlew gradlew.bat ./
COPY build.gradle.kts settings.gradle.kts ./

COPY src src

RUN ./gradlew bootJar

FROM eclipse-temurin:24-jre

WORKDIR /app

COPY --from=builder /app/build/libs/krash-*.jar app.jar

EXPOSE 9090

HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:9090 || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
