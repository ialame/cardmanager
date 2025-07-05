# Stage 1: Build the entire multi-module project
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /usr/src/app

# Copier le POM RACINE en premier # <-- IMPORTANT : Le POM de 220525/
COPY ./pom.xml ./pom.xml

# Copier les POMs des modules
COPY ./mason/pom.xml ./mason/pom.xml
# Si mason a des sous-modules (comme mason-projects), copiez aussi leurs POMs pour le cache
COPY ./mason/mason-projects/ ./mason/mason-projects/

COPY ./painter/pom.xml ./painter/pom.xml
COPY ./gestioncarte/pom.xml ./gestioncarte/pom.xml

# Pour gestioncarte et son frontend
COPY ./gestioncarte/src/main/frontend/package.json ./gestioncarte/src/main/frontend/package.json
COPY ./gestioncarte/src/main/frontend/package-lock.json ./gestioncarte/src/main/frontend/package-lock.json

# Optionnel: Résoudre les dépendances en utilisant le POM RACINE
# RUN mvn -B -f ./pom.xml dependency:go-offline

# Copier TOUT le code source (après les POMs pour le cache)
COPY ./mason ./mason
COPY ./painter ./painter
COPY ./gestioncarte ./gestioncarte
# S'il y a d'autres dossiers à la racine nécessaires au build, copiez-les aussi

# Construire l'ensemble du projet à partir du POM RACINE
RUN mvn -f ./pom.xml clean package -DskipTests

# Stage 2: Create the runtime image for 'painter'
FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="ibrahim.alame@gmail.com"
WORKDIR /app
COPY --from=builder /usr/src/app/painter/painter/target/painter-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]