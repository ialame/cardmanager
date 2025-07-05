#!/bin/bash
# check-jars.sh - Vérifier que les JARs sont présents

echo "🔍 Vérification des JARs construits..."

echo "📁 Structure du projet :"
ls -la

echo ""
echo "📁 Contenu de gestioncarte/target/ :"
if [ -d "gestioncarte/target" ]; then
    ls -la gestioncarte/target/*.jar 2>/dev/null || echo "❌ Aucun JAR trouvé dans gestioncarte/target/"
else
    echo "❌ Répertoire gestioncarte/target/ n'existe pas"
fi

echo ""
echo "📁 Contenu de painter/painter/target/ :"
if [ -d "painter/painter/target" ]; then
    ls -la painter/painter/target/*.jar 2>/dev/null || echo "❌ Aucun JAR trouvé dans painter/painter/target/"
else
    echo "❌ Répertoire painter/painter/target/ n'existe pas"
fi

echo ""
echo "🔍 Recherche de tous les JARs dans le projet :"
find . -name "*.jar" -type f | grep -E "(retriever|painter)" | head -10