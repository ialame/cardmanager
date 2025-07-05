# À placer dans painter/Dockerfile
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Créer un pom.xml temporaire sans le module gestioncarte
COPY pom.xml pom-original.xml
RUN sed '/<module>gestioncarte<\/module>/d' pom-original.xml > pom.xml

# Copier les fichiers de configuration Maven
COPY mason/pom.xml mason/
COPY painter/pom.xml painter/

# Copier les poms des sous-modules de mason
COPY mason/mason-projects/ mason/mason-projects/

# Copier les poms des sous-modules de painter
COPY painter/painter-common/pom.xml painter/painter-common/
COPY painter/painter-client/pom.xml painter/painter-client/
COPY painter/painter/pom.xml painter/painter/

# Télécharger les dépendances
RUN mvn dependency:resolve -pl painter/painter -am

# Copier le code source
COPY mason/ mason/
COPY painter/ painter/

# Build seulement les modules nécessaires pour painter
RUN mvn clean package -pl painter/painter -am -DskipTests

# Stage final
FROM eclipse-temurin:21-jre-jammy

# Installer curl pour les healthchecks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Créer un utilisateur non-root
RUN groupadd --system spring && useradd --system --gid spring spring

# Créer les répertoires
RUN mkdir -p /app/images/storage && chown -R spring:spring /app

# Copier le JAR
COPY --from=build --chown=spring:spring /app/painter/painter/target/painter-*.jar /app/app.jar

USER spring:spring
WORKDIR /app

ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=80.0"

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1