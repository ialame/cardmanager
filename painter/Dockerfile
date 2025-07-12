FROM eclipse-temurin:21-jre-jammy

# Installs...
RUN apt-get update && apt-get install -y curl netcat-openbsd mariadb-client && rm -rf /var/lib/apt/lists/*

# User et dirs...
RUN groupadd --system spring && useradd --system --gid spring spring && \
    mkdir -p /app/images/storage && chown -R spring:spring /app

# Copy JAR pré-build local
COPY painter/painter/target/painter-*.jar /app/app.jar

USER spring:spring
WORKDIR /app

ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=80.0"

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=docker"]

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1