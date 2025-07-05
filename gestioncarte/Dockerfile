# À placer dans gestioncarte/Dockerfile
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Installer Node.js pour le build frontend
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs && \
    rm -rf /var/lib/apt/lists/*

# Copier la structure du projet parent
WORKDIR /app

# Copier les fichiers de configuration Maven en premier (pour le cache des layers)
COPY pom.xml .
COPY mason/pom.xml mason/
COPY painter/pom.xml painter/
COPY gestioncarte/pom.xml gestioncarte/

# Copier les poms des sous-modules de mason
COPY mason/mason-projects/ mason/mason-projects/

# Copier les poms des sous-modules de painter
COPY painter/painter-common/pom.xml painter/painter-common/
COPY painter/painter-client/pom.xml painter/painter-client/
COPY painter/painter/pom.xml painter/painter/

# Télécharger les dépendances (cette layer sera mise en cache)
RUN mvn dependency:go-offline -DexcludeGroupIds=com.pcagrade

# Copier tout le code source
COPY mason/ mason/
COPY painter/ painter/
COPY gestioncarte/ gestioncarte/

# Build du projet complet depuis la racine
RUN mvn clean package -DskipTests

# Stage final - image de production
FROM eclipse-temurin:21-jre-jammy

# Installer curl pour les healthchecks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Créer un utilisateur non-root
RUN groupadd --system spring && useradd --system --gid spring spring

# Créer les répertoires nécessaires
RUN mkdir -p /app/uploads /app/images/storage /app/logs /app/cache && \
    chown -R spring:spring /app

# Copier le JAR depuis le stage de build
COPY --from=build --chown=spring:spring /app/gestioncarte/target/retriever-*.jar /app/app.jar

# Passer à l'utilisateur non-root
USER spring:spring

WORKDIR /app

# Configuration JVM optimisée pour container
ENV JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=80.0"

# Port de l'application
EXPOSE 8080

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1