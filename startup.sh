#!/bin/bash
# startup.sh - Script de démarrage rapide pour CardManager

set -e

echo "🎯 CardManager - Démarrage Docker"
echo "================================="

# Vérifier que Docker est démarré
if ! docker info >/dev/null 2>&1; then
    echo "❌ Docker n'est pas démarré. Veuillez démarrer Docker Desktop."
    exit 1
fi

# Vérifier que docker-compose est disponible
if ! command -v docker-compose &> /dev/null; then
    echo "❌ docker-compose n'est pas installé."
    exit 1
fi

echo "✅ Docker est prêt"

# Nettoyer les containers précédents si demandé
if [ "$1" = "--clean" ]; then
    echo "🧹 Nettoyage des containers précédents..."
    docker-compose -f docker-compose.simple.yml down -v --remove-orphans 2>/dev/null || true
    docker system prune -f >/dev/null 2>&1 || true
fi

# Build des JARs avec Maven
echo "🔨 Construction des JARs avec Maven..."
mvn clean package -DskipTests

# Construire les images Docker
echo "🐳 Construction des images Docker..."
docker-compose -f docker-compose.simple.yml build

# Démarrer les services
echo "🚀 Démarrage des services..."
docker-compose -f docker-compose.simple.yml up -d

# Attendre que les services soient prêts
echo "⏳ Attente du démarrage des services..."
sleep 10

# Vérifier le statut
echo "📊 Statut des services:"
docker-compose -f docker-compose.simple.yml ps

# Vérifier la santé des services
echo ""
echo "🏥 Vérification de la santé des services..."

# Attendre que la base soit prête
echo "⏳ Attente de la base de données..."
timeout 60 bash -c 'until docker-compose -f docker-compose.simple.yml exec -T database mysql -u carduser -pcardpassword -e "SELECT 1;" >/dev/null 2>&1; do sleep 2; done'

# Attendre que les applications soient prêtes
echo "⏳ Attente des applications..."
timeout 120 bash -c 'until curl -f http://localhost:8080/actuator/health >/dev/null 2>&1; do sleep 5; done'
timeout 60 bash -c 'until curl -f http://localhost:8081/actuator/health >/dev/null 2>&1; do sleep 5; done'

echo ""
echo "✅ CardManager est prêt !"
echo ""
echo "🌐 URLs disponibles:"
echo "   • Application principale: http://localhost:8080"
echo "   • Service Painter:       http://localhost:8081"
echo "   • Admin DB (Adminer):    http://localhost:8082"
echo ""
echo "📋 Commandes utiles:"
echo "   • Voir les logs:         docker-compose -f docker-compose.simple.yml logs -f"
echo "   • Arrêter:              docker-compose -f docker-compose.simple.yml down"
echo "   • Status:               docker-compose -f docker-compose.simple.yml ps"
echo ""
echo "🎉 Bon développement !"