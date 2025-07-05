#!/bin/bash
# quick-fix.sh - Build rapide sans les fichiers Docker

echo "🚀 Build rapide sans profil Docker..."

# Supprimer temporairement les fichiers problématiques
if [ -f "painter/painter/src/main/resources/application-docker.properties" ]; then
    echo "🗑️ Suppression temporaire du fichier problématique"
    mv "painter/painter/src/main/resources/application-docker.properties" "painter/painter/src/main/resources/application-docker.properties.bak" 2>/dev/null || true
fi

# Build Maven sans les tests
echo "🔨 Build Maven..."
mvn clean package -DskipTests

echo "✅ Build terminé !"
echo ""
echo "🐳 Maintenant, lancez Docker avec:"
echo "   docker-compose -f docker-compose.simple.yml up --build -d"