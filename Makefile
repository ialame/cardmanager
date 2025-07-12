# Makefile pour CardManager Docker (sans gestion de base de données)

.PHONY: help build up down logs clean rebuild restart status shell

# Variables
COMPOSE_FILE = docker-compose.yml
PROJECT_NAME = cardmanager

help: ## Afficher l'aide
	@echo "Commandes disponibles:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-15s\033[0m %s\n", $$1, $$2}'

build: ## Construire toutes les images Docker
	@echo "🔨 Construction des images Docker..."
	@echo "⚠️  Note: La base de données externe doit être démarrée (cardmanager-database)"
	docker-compose -f $(COMPOSE_FILE) build --no-cache

up: ## Démarrer tous les services
	@echo "🚀 Démarrage des services..."
	@echo "⚠️  Note: Vérifiez que la base de données externe est accessible (cardmanager-database)"
	docker-compose -f $(COMPOSE_FILE) up -d

down: ## Arrêter tous les services
	@echo "🛑 Arrêt des services..."
	@echo "⚠️  Note: La base de données externe ne sera PAS arrêtée"
	docker-compose -f $(COMPOSE_FILE) down

logs: ## Afficher les logs de tous les services
	docker-compose -f $(COMPOSE_FILE) logs -f

logs-retriever: ## Afficher les logs du service retriever
	docker-compose -f $(COMPOSE_FILE) logs -f retriever

logs-painter: ## Afficher les logs du service painter
	docker-compose -f $(COMPOSE_FILE) logs -f painter

clean: ## Nettoyer les containers, volumes et images (sans la base de données)
	@echo "🧹 Nettoyage des containers (base de données externe préservée)..."
	docker-compose -f $(COMPOSE_FILE) down -v --rmi local --remove-orphans
	docker system prune -f

rebuild: clean build up ## Nettoyer, reconstruire et redémarrer

restart: ## Redémarrer tous les services
	docker-compose -f $(COMPOSE_FILE) restart

status: ## Afficher le statut des services
	docker-compose -f $(COMPOSE_FILE) ps
	@echo ""
	@echo "📋 Vérification de la base de données externe:"
	@docker ps --filter "name=cardmanager-db-test" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" || echo "❌ Container cardmanager-db-test non trouvé"

shell-retriever: ## Ouvrir un shell dans le container retriever
	docker-compose -f $(COMPOSE_FILE) exec retriever sh

shell-painter: ## Ouvrir un shell dans le container painter
	docker-compose -f $(COMPOSE_FILE) exec painter sh

test-db: ## Tester la connexion à la base de données externe
	@echo "🔍 Test de connexion à la base de données externe..."
	@docker exec cardmanager-db-test mariadb -u ia -pfoufafou -e "SELECT 'Connection OK' as status, DATABASE() as current_db;" cardmanager 2>/dev/null && echo "✅ Base de données accessible" || echo "❌ Problème de connexion à la base"

dev: ## Mode développement avec rebuild automatique
	@echo "🔨 Build Maven..."
	mvn clean package -DskipTests
	docker-compose -f $(COMPOSE_FILE) up --build

prod: ## Mode production (build optimisé)
	docker-compose -f $(COMPOSE_FILE) up -d --build

connect-external-db: ## Connecter les services au réseau de la base de données externe
	@echo "🔗 Connexion au réseau de la base de données externe..."
	@docker network connect cardmanager-network cardmanager-db-test 2>/dev/null && echo "✅ Base connectée au réseau" || echo "⚠️  Base déjà connectée ou erreur réseau"