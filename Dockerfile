# Multi-stage build: Gradle compiles, slim JRE runs.
# Render's "docker" runtime reads this; runtime: docker in render.yaml.

FROM gradle:8.10-jdk17 AS build
WORKDIR /home/gradle/src

# Cache dependencies first
COPY --chown=gradle:gradle build.gradle.kts settings.gradle.kts gradle.properties ./
COPY --chown=gradle:gradle config ./config
RUN gradle --no-daemon dependencies > /dev/null 2>&1 || true

# Now build
COPY --chown=gradle:gradle src ./src
RUN gradle --no-daemon shadowJar

# Runtime: slim JRE only
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/ad-api-all-0.1.0.jar /app/ad-api-all.jar
COPY --from=build /home/gradle/src/config /app/config

ENV PORT=10000
ENV PROVIDER_CONFIG_PATH=config/providers.yaml
EXPOSE 10000
CMD ["java", "-jar", "/app/ad-api-all.jar"]
