# Stage de build
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copier le POM parent et les sous-modules mason
COPY pom.xml .
COPY mason/pom.xml mason/
COPY mason/mason-projects/ mason/mason-projects/

# Télécharger les dépendances pour cache
RUN mvn dependency:go-offline

# Copier les sources
COPY mason/ mason/

# Build mason et ses sous-modules
RUN mvn clean package -pl :mason-parent -am -DskipTests  # Adaptez si POM parent est différent

# Stage final (optionnel, si vous voulez une image avec les JARs)
FROM eclipse-temurin:21-jre-jammy

# Copier les JARs construits (ex. : pour deployment ou test)
COPY --from=build /app/mason/target/*.jar /app/lib/

WORKDIR /app

# Pas d'ENTRYPOINT nécessaire pour une lib, mais vous pouvez ajouter un CMD pour tests
CMD ["ls", "-l", "/app/lib"]