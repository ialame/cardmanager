#!/bin/bash
# quick-start.sh - Script de démarrage rapide CardManager
# Usage: ./quick-start.sh [--clean] [--production]

set -e

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration par défaut
CLEAN_INSTALL=false
PRODUCTION_MODE=false
PROJECT_NAME="cardmanager"

# Traitement des arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --clean)
            CLEAN_INSTALL=true
            shift
            ;;
        --production)
            PRODUCTION_MODE=true
            shift
            ;;
        -h|--help)
            echo "Usage: $0 [--clean] [--production]"
            echo "  --clean      Nettoyage complet avant installation"
            echo "  --production Mode production"
            exit 0
            ;;
        *)
            echo "Option inconnue: $1"
            exit 1
            ;;
    esac
done

# Fonctions utilitaires
log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

check_command() {
    if ! command -v $1 &> /dev/null; then
        log_error "$1 n'est pas installé"
        exit 1
    fi
}

# Vérification des prérequis
check_requirements() {
    log_info "Vérification des prérequis..."

    check_command "java"
    check_command "mvn"
    check_command "docker"
    check_command "docker-compose"

    # Vérifier les versions
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        log_error "Java 17+ requis (trouvé: $JAVA_VERSION)"
        exit 1
    fi

    # Vérifier que Docker fonctionne
    if ! docker info >/dev/null 2>&1; then
        log_error "Docker n'est pas démarré"
        exit 1
    fi

    log_success "Tous les prérequis sont satisfaits"
}

# Configuration de la base de données
setup_database() {
    log_info "Configuration de la base de données..."

    # Vérifier si une base externe existe
    read -p "Avez-vous une base MariaDB/MySQL externe? (y/n): " -r
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo
        read -p "Host de la base (localhost): " DB_HOST
        DB_HOST=${DB_HOST:-localhost}

        read -p "Port de la base (3306): " DB_PORT
        DB_PORT=${DB_PORT:-3306}

        read -p "Nom de la base (dev): " DB_NAME
        DB_NAME=${DB_NAME:-dev}

        read -p "Utilisateur de la base (ia): " DB_USER
        DB_USER=${DB_USER:-ia}

        read -s -p "Mot de passe de la base: " DB_PASSWORD
        echo

        # Tester la connexion
        if command -v mysql &> /dev/null; then
            if mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" -e "SELECT 1;" "$DB_NAME" >/dev/null 2>&1; then
                log_success "Connexion à la base réussie"
            else
                log_warning "Impossible de se connecter à la base (vérifiez les paramètres)"
            fi
        fi

        # Mettre à jour docker-compose.yml avec les vraies valeurs
        update_database_config "$DB_HOST" "$DB_PORT" "$DB_NAME" "$DB_USER" "$DB_PASSWORD"
    else
        log_info "Création d'une base Docker temporaire pour les tests..."
        create_docker_database
    fi
}

# Mise à jour de la configuration base de données
update_database_config() {
    local db_host=$1
    local db_port=$2
    local db_name=$3
    local db_user=$4
    local db_password=$5

    # Construire l'URL de connexion
    local db_url="jdbc:mariadb://${db_host}:${db_port}/${db_name}?useUnicode=yes&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&noAccessToProcedureBodies=true"

    # Mettre à jour les fichiers de configuration
    if [ -f "painter/painter/src/main/resources/application-docker.properties" ]; then
        sed -i.bak "s|spring.datasource.url=.*|spring.datasource.url=${db_url}|g" painter/painter/src/main/resources/application-docker.properties
        sed -i.bak "s|spring.datasource.username=.*|spring.datasource.username=${db_user}|g" painter/painter/src/main/resources/application-docker.properties
        sed -i.bak "s|spring.datasource.password=.*|spring.datasource.password=${db_password}|g" painter/painter/src/main/resources/application-docker.properties
    fi

    if [ -f "gestioncarte/src/main/resources/application-docker.properties" ]; then
        sed -i.bak "s|spring.datasource.url=.*|spring.datasource.url=${db_url}|g" gestioncarte/src/main/resources/application-docker.properties
        sed -i.bak "s|spring.datasource.username=.*|spring.datasource.username=${db_user}|g" gestioncarte/src/main/resources/application-docker.properties
        sed -i.bak "s|spring.datasource.password=.*|spring.datasource.password=${db_password}|g" gestioncarte/src/main/resources/application-docker.properties
    fi

    log_success "Configuration de la base mise à jour"
}

# Création d'une base Docker temporaire
create_docker_database() {
    docker run -d \
        --name "${PROJECT_NAME}-database-temp" \
        -e MYSQL_ROOT_PASSWORD=root123 \
        -e MYSQL_DATABASE=dev \
        -e MYSQL_USER=ia \
        -e MYSQL_PASSWORD=foufafou \
        -p 3307:3306 \
        --restart unless-stopped \
        mariadb:11.4 >/dev/null 2>&1

    log_success "Base de données temporaire créée sur le port 3307"
    sleep 10  # Attendre que la base soit prête
}

# Nettoyage
cleanup() {
    if [ "$CLEAN_INSTALL" = true ]; then
        log_info "Nettoyage complet..."

        # Arrêter et supprimer les containers
        docker-compose -f docker-compose.yml down -v --remove-orphans 2>/dev/null || true
        docker-compose -f docker-compose.simple.yml down -v --remove-orphans 2>/dev/null || true

        # Supprimer les images
        docker images --filter "reference=${PROJECT_NAME}*" --format "{{.ID}}" | xargs -r docker rmi -f 2>/dev/null || true

        # Nettoyer le cache Maven
        mvn clean >/dev/null 2>&1 || true

        # Supprimer les fichiers temporaires
        find . -name "*.bak" -delete 2>/dev/null || true

        log_success "Nettoyage terminé"
    fi
}

# Build de l'application
build_application() {
    log_info "Construction de l'application..."

    # Build Maven
    if mvn clean package -DskipTests -q; then
        log_success "Build Maven réussi"
    else
        log_error "Échec du build Maven"
        exit 1
    fi

    # Vérifier que les JARs existent
    if [ ! -f "gestioncarte/target/retriever-"*.jar ]; then
        log_error "JAR retriever non trouvé"
        exit 1
    fi

    if [ ! -f "painter/painter/target/painter-"*.jar ]; then
        log_error "JAR painter non trouvé"
        exit 1
    fi

    log_success "JARs créés avec succès"
}

# Démarrage des services
start_services() {
    log_info "Démarrage des services Docker..."

    # Choisir le bon fichier docker-compose
    local compose_file="docker-compose.yml"
    if [ "$PRODUCTION_MODE" = true ]; then
        compose_file="docker-compose.prod.yml"
    fi

    # Vérifier que le fichier existe
    if [ ! -f "$compose_file" ]; then
        log_error "Fichier $compose_file non trouvé"
        exit 1
    fi

    # Démarrer les services
    if docker-compose -f "$compose_file" up --build -d; then
        log_success "Services démarrés"
    else
        log_error "Échec du démarrage des services"
        exit 1
    fi

    # Attendre que les services soient prêts
    log_info "Attente du démarrage des services..."
    sleep 30

    # Vérifier la santé des services
    check_services_health "$compose_file"
}

# Vérification de la santé des services
check_services_health() {
    local compose_file=$1

    log_info "Vérification de la santé des services..."

    # Status des containers
    docker-compose -f "$compose_file" ps

    # Test des endpoints
    local endpoints=(
        "http://localhost:8080/actuator/health|Retriever"
        "http://localhost:8081/actuator/health|Painter"
        "http://localhost:8083/images/|Images"
    )

    for endpoint_info in "${endpoints[@]}"; do
        IFS='|' read -r endpoint name <<< "$endpoint_info"
        if curl -f "$endpoint" >/dev/null 2>&1; then
            log_success "$name accessible"
        else
            log_warning "$name non accessible ($endpoint)"
        fi
    done
}

# Affichage des informations finales
show_final_info() {
    echo
    echo "🎉 CardManager est maintenant déployé !"
    echo
    echo "📋 URLs d'accès :"
    echo "  🌐 Application principale : http://localhost:8080"
    echo "  🖼️  Visionneuse d'images  : http://localhost:8083/image-viewer.html"
    echo "  📁 Navigation images     : http://localhost:8083/images/cards/pokemon/"
    echo "  🗄️  Administration BDD   : http://localhost:8082"
    echo
    echo "🔧 Commandes utiles :"
    echo "  make status              # Voir le statut des services"
    echo "  make logs               # Voir les logs"
    echo "  make restart            # Redémarrer les services"
    echo "  make clean              # Nettoyer complètement"
    echo
    echo "📚 Documentation :"
    echo "  README.md               # Guide utilisateur"
    echo "  DEPLOYMENT.md           # Guide de déploiement"
    echo
    if [ "$PRODUCTION_MODE" = false ]; then
        echo "💡 Pour la production, relancez avec : $0 --production"
    fi
    echo
}

# Gestion des erreurs
handle_error() {
    log_error "Une erreur s'est produite. Voir les logs ci-dessus."
    echo
    echo "🆘 Aide au dépannage :"
    echo "  1. Vérifiez que Docker fonctionne : docker info"
    echo "  2. Vérifiez l'espace disque : df -h"
    echo "  3. Consultez les logs : docker-compose logs"
    echo "  4. Nettoyez et recommencez : $0 --clean"
    exit 1
}

# Main
main() {
    # Configuration du gestionnaire d'erreur
    trap handle_error ERR

    echo "🚀 CardManager - Script de Démarrage Rapide"
    echo "============================================="
    echo

    if [ "$PRODUCTION_MODE" = true ]; then
        log_info "Mode PRODUCTION activé"
    else
        log_info "Mode DÉVELOPPEMENT (ajoutez --production pour la prod)"
    fi
    echo

    # Étapes de déploiement
    check_requirements
    cleanup
    setup_database
    build_application
    start_services
    show_final_info

    log_success "Déploiement terminé avec succès ! 🎉"
}

# Exécution
main "$@"