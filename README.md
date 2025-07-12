# 🃏 CardManager - Gestionnaire de Cartes à Collectionner

![Version](https://img.shields.io/badge/version-9.4.0-blue.svg)
![Java](https://img.shields.io/badge/java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/spring--boot-3.2.5-brightgreen.svg)
![Docker](https://img.shields.io/badge/docker-ready-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

## 📋 Table des Matières

- [Description](#-description)
- [Architecture](#-architecture)
- [Fonctionnalités](#-fonctionnalités)
- [Prérequis](#-prérequis)
- [Installation Rapide](#-installation-rapide)
- [Guide de Déploiement](#-guide-de-déploiement)
- [Utilisation](#-utilisation)
- [API](#-api)
- [Développement](#-développement)
- [Troubleshooting](#-troubleshooting)
- [Contribution](#-contribution)
- [Support](#-support)

## 🎯 Description

CardManager est une application web complète pour la gestion de cartes à collectionner (Pokemon, Yu-Gi-Oh!, One Piece, etc.). Elle permet l'importation, le stockage, la visualisation et la gestion d'images de cartes avec une architecture microservices containerisée.

### ✨ Points Forts

- 🏗️ **Architecture Microservices** modulaire et scalable
- 🐳 **Containerisation Docker** pour un déploiement simplifié
- 🎨 **Interface Moderne** avec visionneuse d'images intégrée
- 📊 **Base de Données** unifiée pour tous les types de cartes
- 🔒 **Sécurité** intégrée avec gestion des permissions
- 📡 **API REST** complète pour l'intégration
- 🖼️ **Serveur d'Images** haute performance

## 🏗️ Architecture

```
CardManager/
├── 📦 mason/                    # Modules communs et utilitaires
├── 🎨 painter/                  # Service de gestion d'images
│   └── painter/                 # Module principal Painter
├── 🃏 gestioncarte/            # Service principal (Retriever)
└── 🐳 docker/                  # Configuration Docker
```

### Services

| Service | Port | Description |
|---------|------|-------------|
| **Retriever** | 8080 | Application principale et API |
| **Painter** | 8081 | Service de gestion d'images |
| **Image Server** | 8083 | Serveur web pour visualisation |
| **Adminer** | 8082 | Interface d'administration BDD |

## 🚀 Fonctionnalités

### 🎴 Gestion des Cartes
- Import automatique depuis les sites officiels
- Support multi-jeux (Pokemon, Yu-Gi-Oh!, One Piece)
- Gestion des métadonnées et attributs
- Historique des modifications

### 🖼️ Gestion des Images
- Stockage optimisé avec organisation par dossiers
- Support formats : PNG, JPG, JPEG, WEBP
- Compression et redimensionnement automatique
- Serveur d'images haute performance

### 🔍 Recherche et Navigation
- Recherche avancée multi-critères
- Filtrage par jeu, série, type
- Interface de navigation intuitive
- Visionneuse d'images moderne

### 📊 Administration
- Interface d'administration complète
- Statistiques et métriques
- Gestion des utilisateurs et permissions
- Monitoring des services

## 📋 Prérequis

### Système
- **OS** : macOS, Linux, Windows 10+
- **RAM** : 4GB minimum, 8GB recommandé
- **Stockage** : 2GB pour l'application + espace pour les images

### Logiciels
- **Java 21** ou supérieur
- **Maven 3.8+** pour le build
- **Docker** et **Docker Compose** pour l'exécution
- **Git** pour le versioning

### Base de Données Externe
- **MariaDB 11.4+** ou **MySQL 8.0+**
- Utilisateur avec privilèges CREATE/ALTER
- Réseau accessible depuis Docker

## ⚡ Installation Rapide

### 1. Cloner le Projet

```bash
git clone https://github.com/votre-username/cardmanager.git
cd cardmanager
```

### 2. Configuration de la Base de Données

Assurez-vous d'avoir une base MariaDB/MySQL accessible :

```sql
-- Créer la base et l'utilisateur
CREATE DATABASE dev;
CREATE USER 'ia'@'%' IDENTIFIED BY 'foufafou';
GRANT ALL PRIVILEGES ON dev.* TO 'ia'@'%';
FLUSH PRIVILEGES;
```

### 3. Lancement Automatique

```bash
# Démarrage complet avec un seul script
./startup.sh

# Ou étape par étape
make rebuild
```

### 4. Accès aux Services

- 🌐 **Application** : http://localhost:8080
- 🖼️ **Visionneuse** : http://localhost:8083/image-viewer.html
- 🗄️ **Admin BDD** : http://localhost:8082

## 📖 Guide de Déploiement

Voir le fichier [DEPLOYMENT.md](DEPLOYMENT.md) pour un guide complet de déploiement.

## 💻 Utilisation

### Interface Web

1. **Accéder à l'application** : http://localhost:8080
2. **Importer des cartes** via les connecteurs automatiques
3. **Visualiser les images** : http://localhost:8083/image-viewer.html
4. **Administrer** via Adminer : http://localhost:8082

### Visionneuse d'Images

La visionneuse d'images offre :
- 🔍 **Recherche** par nom, ID ou dossier
- 📊 **Statistiques** en temps réel
- 🖼️ **Visualisation** en plein écran
- ⬇️ **Téléchargement** d'images
- 📱 **Interface responsive**

### API REST

```bash
# Santé des services
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health

# API images
curl http://localhost:8081/api/images/

# Métriques
curl http://localhost:8080/actuator/metrics
```

## 🔧 Développement

### Build Local

```bash
# Build complet
mvn clean package -DskipTests

# Build par module
mvn clean package -pl mason -am -DskipTests
mvn clean package -pl painter/painter -am -DskipTests
mvn clean package -pl gestioncarte -am -DskipTests
```

### Développement avec Docker

```bash
# Mode développement
make dev

# Logs en temps réel
make logs

# Rebuild rapide
make restart
```

### Structure des Données

```
Images : /app/images/storage/cards/pokemon/[3chars]/[ULID].png
Logs   : /app/logs/
Cache  : /app/cache/
Uploads: /app/uploads/
```

## 🛠️ Troubleshooting

### Problèmes Courants

**Services ne démarrent pas**
```bash
# Vérifier les logs
make logs
docker logs cardmanager-painter
docker logs cardmanager-retriever
```

**Base de données inaccessible**
```bash
# Tester la connexion
docker exec cardmanager-db-test mariadb -u ia -pfoufafou -e "SELECT 1;" dev
```

**Images non visibles**
```bash
# Vérifier les permissions
docker exec -it cardmanager-painter ls -la /app/images/storage/
```

**Port déjà utilisé**
```bash
# Modifier les ports dans docker-compose.yml
ports:
  - "8090:8080"  # Changer 8080 en 8090
```

### Commandes Utiles

```bash
# Status complet
make status

# Nettoyage complet
make clean

# Accès shell containers
docker exec -it cardmanager-painter sh
docker exec -it cardmanager-retriever sh

# Backup base de données
make backup-db
```

## 🤝 Contribution

1. **Fork** le projet
2. **Créer** une branche feature (`git checkout -b feature/amazing-feature`)
3. **Commit** vos changements (`git commit -m 'Add amazing feature'`)
4. **Push** vers la branche (`git push origin feature/amazing-feature`)
5. **Ouvrir** une Pull Request

### Standards de Code

- Java 21+ avec Spring Boot 3.x
- Tests unitaires avec JUnit 5
- Documentation JavaDoc
- Respect des conventions Maven

### Roadmap

- [ ] Support PostgreSQL
- [ ] Interface d'administration avancée
- [ ] API GraphQL
- [ ] Notifications en temps réel
- [ ] Import en masse
- [ ] Export vers formats standards

## 📞 Support

### Documentation

- 📚 [Wiki Complet](https://github.com/votre-username/cardmanager/wiki)
- 🔧 [Guide API](docs/API.md)
- 🐳 [Docker Guide](docs/DOCKER.md)

### Contact

- 🐛 **Issues** : [GitHub Issues](https://github.com/votre-username/cardmanager/issues)
- 💬 **Discussions** : [GitHub Discussions](https://github.com/votre-username/cardmanager/discussions)
- 📧 **Email** : support@cardmanager.dev

## 📄 License

Ce projet est sous licence MIT. Voir [LICENSE](LICENSE) pour plus de détails.

---

<div align="center">

**🎮 Fait avec ❤️ pour la communauté des collectionneurs**

[⭐ Star ce projet](https://github.com/votre-username/cardmanager) • [🐛 Reporter un bug](https://github.com/votre-username/cardmanager/issues) • [💡 Suggérer une feature](https://github.com/votre-username/cardmanager/issues)

</div>
# cardmanager
# cardmanager
