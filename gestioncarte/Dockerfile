# Stage final - image de production (après build local)
FROM eclipse-temurin:21-jre-jammy

# Installer curl pour healthchecks et mariadb-client pour débogage
RUN apt-get update && apt-get install -y curl netcat-openbsd mariadb-client && rm -rf /var/lib/apt/lists/*

# Créer utilisateur non-root et répertoires
RUN groupadd --system spring && useradd --system --gid spring spring && \
    mkdir -p /app/uploads /app/logs && chown -R spring:spring /app

# Copier le JAR pré-construit localement
COPY gestioncarte/target/retriever-*.jar /app/app.jar

USER spring:spring
WORKDIR /app

ENV JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=80.0"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=docker"]

HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1