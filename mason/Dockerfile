FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# 1. Copier le POM parent
COPY ./pom.xml .

# 2. Copier les POMs des sous-modules (en utilisant des chemins relatifs corrects)
COPY ./mason-projects/mason-commons/pom.xml ./mason-projects/mason-commons/
COPY ./mason-projects/mason-jpa/pom.xml ./mason-projects/mason-jpa/
# Ajoutez ici tous les autres modules Mason nécessaires

# 3. Télécharger les dépendances
RUN mvn dependency:go-offline -B

# 4. Copier les sources (en utilisant des chemins relatifs corrects)
COPY ./mason-projects/mason-commons/src ./mason-projects/mason-commons/src
COPY ./mason-projects/mason-jpa/src ./mason-projects/mason-jpa/src
# Ajoutez ici tous les autres modules Mason nécessaires

# 5. Construire le projet
RUN mvn clean install -DskipTests