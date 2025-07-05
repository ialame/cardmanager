# Makefile pour CardManager Docker

.PHONY: help build up down logs clean rebuild restart status shell

# Variables
COMPOSE_FILE = docker-compose.yml
PROJECT_NAME = cardmanager

help: ## Afficher l'aide
	@echo "Commandes disponibles:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-15s\033[0m %s\n", $$1, $$2}'

build: ## Construire toutes les images Docker
	@echo "🔨 Construction des images Docker..."
	docker-compose -f $(COMPOSE_FILE) build --no-cache

up: ## Démarrer tous les services
	@echo "🚀 Démarrage des services..."
	docker-compose -f $(COMPOSE_FILE) up -d

down: ## Arrêter tous les services
	@echo "🛑 Arrêt des services..."
	docker-compose -f $(COMPOSE_FILE) down

logs: ## Afficher les logs de tous les services
	docker-compose -f $(COMPOSE_FILE) logs -f

logs-retriever: ## Afficher les logs du service retriever
	docker-compose -f $(COMPOSE_FILE) logs -f retriever

logs-painter: ## Afficher les logs du service painter
	docker-compose -f $(COMPOSE_FILE) logs -f painter

logs-db: ## Afficher les logs de la base de données
	docker-compose -f $(COMPOSE_FILE) logs -f database

clean: ## Nettoyer les containers, volumes et images
	docker-compose -f $(COMPOSE_FILE) down -v --rmi all --remove-orphans
	docker system prune -f

rebuild: clean build up ## Nettoyer, reconstruire et redémarrer

restart: ## Redémarrer tous les services
	docker-compose -f $(COMPOSE_FILE) restart

status: ## Afficher le statut des services
	docker-compose -f $(COMPOSE_FILE) ps

shell-retriever: ## Ouvrir un shell dans le container retriever
	docker-compose -f $(COMPOSE_FILE) exec retriever sh

shell-painter: ## Ouvrir un shell dans le container painter
	docker-compose -f $(COMPOSE_FILE) exec painter sh

shell-db: ## Ouvrir un shell MySQL dans la base de données
	docker-compose -f $(COMPOSE_FILE) exec database mysql -u carduser -p cardmanager

backup-db: ## Sauvegarder la base de données
	docker-compose -f $(COMPOSE_FILE) exec -T database mysqldump -u carduser -p cardmanager > backup_$(shell date +%Y%m%d_%H%M%S).sql

dev: ## Mode développement avec rebuild automatique
	docker-compose -f $(COMPOSE_FILE) up --build

prod: ## Mode production (build optimisé)
	docker-compose -f $(COMPOSE_FILE) -f docker-compose.prod.yml up -d --build