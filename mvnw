#!/bin/bash
# À placer à la racine du projet : mvnw

# Script wrapper Maven pour Docker
# Assure-t-il que Maven fonctionne dans le conteneur

set -e

# Vérifier si Maven est installé
if ! command -v mvn &> /dev/null; then
    echo "Maven not found. Installing..."

    # Télécharger et installer Maven
    MAVEN_VERSION=3.9.6
    cd /tmp
    wget -q https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz
    tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz
    mv apache-maven-${MAVEN_VERSION} /opt/maven

    # Créer les liens symboliques
    ln -s /opt/maven/bin/mvn /usr/local/bin/mvn

    # Nettoyer
    rm -f apache-maven-${MAVEN_VERSION}-bin.tar.gz
    cd /app
fi

# Exécuter Maven avec les arguments passés
exec mvn "$@"