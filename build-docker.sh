#!/bin/bash
# build-docker.sh - Script pour construire les images séparément

set -e

echo "🔨 Construction des modules Maven..."

# Build Mason d'abord
echo "📦 Building Mason..."
mvn clean package -pl mason -am -DskipTests

# Build Painter
echo "🎨 Building Painter..."
mvn clean package -pl painter/painter -am -DskipTests

# Build Gestioncarte (Retriever)
echo "🃏 Building Retriever..."
mvn clean package -pl gestioncarte -am -DskipTests

echo "✅ Tous les modules sont construits"

# Maintenant construire les images Docker
echo "🐳 Construction des images Docker..."

# Copier les JARs dans des répertoires temporaires pour Docker
mkdir -p docker-build/retriever docker-build/painter

cp gestioncarte/target/retriever-*.jar docker-build/retriever/app.jar
cp painter/painter/target/painter-*.jar docker-build/painter/app.jar

echo "✅ Build terminé avec succès"