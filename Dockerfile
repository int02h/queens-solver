# ---------- Build stage (uniform JDK, works on amd64/arm64) ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Keep Gradle caches inside the layer for faster rebuilds
ENV GRADLE_USER_HOME=/workspace/.gradle

# Copy only what's needed to warm dependency cache
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle

# Show arch/JDK (handy if you ever need to debug)
RUN uname -m && java -version

# Warm Gradle and dependency metadata (non-fatal on cache miss)
RUN ./gradlew --no-daemon --version || true
RUN ./gradlew --no-daemon dependencies || true

# Now bring in sources and build a fat JAR (shadowJar or bootJar)
COPY src ./src
RUN ./gradlew --no-daemon clean shadowJar

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar
COPY --from=build /workspace/build/libs/*-all.jar /app/app.jar
COPY web-content /app/web-content
COPY field-db /app/field-db

EXPOSE 54411
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar server 54411"]
