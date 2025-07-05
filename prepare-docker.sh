#!/bin/bash
# prepare-docker.sh - Préparer les JARs pour Docker

echo "🔨 Préparation pour Docker..."

# Vérifier que le build Maven a été fait
if [ ! -f "gestioncarte/target/retriever-9.4.0.jar" ]; then
    echo "❌ JAR retriever non trouvé. Lancement du build Maven..."
    mvn clean package -DskipTests
fi

# Vérifier la présence des JARs
echo "🔍 Vérification des JARs..."

RETRIEVER_JAR=$(find gestioncarte/target -name "retriever-*.jar" -type f | head -1)
PAINTER_JAR=$(find painter/painter/target -name "painter-*.jar" -type f | head -1)

if [ -z "$RETRIEVER_JAR" ]; then
    echo "❌ JAR Retriever non trouvé !"
    exit 1
fi

if [ -z "$PAINTER_JAR" ]; then
    echo "❌ JAR Painter non trouvé !"
    exit 1
fi

echo "✅ JAR Retriever trouvé : $RETRIEVER_JAR"
echo "✅ JAR Painter trouvé : $PAINTER_JAR"

# Lancer Docker Compose
echo "🐳 Lancement de Docker Compose..."
docker-compose -f docker-compose.simple.yml up --build -d

echo "📊 Statut des services :"
sleep 5
docker-compose -f docker-compose.simple.yml ps