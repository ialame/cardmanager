# 🚀 Guide de Déploiement CardManager

Ce guide vous accompagne étape par étape pour déployer CardManager en environnement de développement et production.

## 📋 Table des Matières

- [Prérequis](#-prérequis)
- [Déploiement Développement](#-déploiement-développement)
- [Déploiement Production](#-déploiement-production)
- [Configuration Base de Données](#-configuration-base-de-données)
- [Sécurité](#-sécurité)
- [Monitoring](#-monitoring)
- [Maintenance](#-maintenance)
- [Dépannage](#-dépannage)

## ✅ Prérequis

### Environnement Système

| Composant | Minimum | Recommandé |
|-----------|---------|------------|
| **CPU** | 2 cores | 4+ cores |
| **RAM** | 4GB | 8GB+ |
| **Stockage** | 10GB | 50GB+ SSD |
| **Réseau** | 100Mbps | 1Gbps |

### Logiciels Requis

```bash
# Vérifier les versions
java --version    # Java 21+
mvn --version     # Maven 3.8+
docker --version  # Docker 20.10+
docker-compose --version  # Docker Compose 2.0+
```

### Base de Données

- **MariaDB 11.4+** ou **MySQL 8.0+**
- **2GB RAM** minimum pour la base
- **Réseau accessible** depuis les containers Docker

## 🛠️ Déploiement Développement

### 1. Préparation de l'Environnement

```bash
# Cloner le projet
git clone https://github.com/votre-username/cardmanager.git
cd cardmanager

# Vérifier l'environnement
./check-requirements.sh  # Si disponible
```

### 2. Configuration de la Base de Données

#### Option A : Base Externe Existante

```bash
# Se connecter à votre base MariaDB/MySQL
mysql -u root -p

# Créer la base et l'utilisateur
CREATE DATABASE dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'ia'@'%' IDENTIFIED BY 'foufafou';
GRANT ALL PRIVILEGES ON dev.* TO 'ia'@'%';
FLUSH PRIVILEGES;
```

#### Option B : Base avec Docker

```bash
# Créer une base MariaDB temporaire
docker run -d \
  --name cardmanager-database \
  -e MYSQL_ROOT_PASSWORD=root123 \
  -e MYSQL_DATABASE=dev \
  -e MYSQL_USER=ia \
  -e MYSQL_PASSWORD=foufafou \
  -p 3307:3306 \
  mariadb:11.4
```

### 3. Configuration des Services

Modifier `docker-compose.yml` selon votre environnement :

```yaml
# Exemple pour base externe
services:
  painter:
    environment:
      SPRING_DATASOURCE_URL: jdbc:mariadb://votre-host:3306/dev?useUnicode=yes&characterEncoding=UTF-8
      SPRING_DATASOURCE_USERNAME: ia
      SPRING_DATASOURCE_PASSWORD: foufafou
```

### 4. Démarrage des Services

```bash
# Méthode automatique (recommandée)
./startup.sh

# Ou méthode manuelle
mvn clean package -DskipTests
docker-compose up --build -d

# Vérifier le statut
make status
```

### 5. Validation du Déploiement

```bash
# Tester les endpoints
curl -f http://localhost:8080/actuator/health
curl -f http://localhost:8081/actuator/health

# Accéder aux interfaces
open http://localhost:8080           # Application
open http://localhost:8083/image-viewer.html  # Visionneuse
open http://localhost:8082           # Adminer
```

## 🏭 Déploiement Production

### 1. Préparation Serveur

#### Serveur Linux (Ubuntu 22.04 LTS)

```bash
# Mise à jour système
sudo apt update && sudo apt upgrade -y

# Installation Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Installation Docker Compose
sudo apt install docker-compose-plugin

# Java pour les builds (optionnel)
sudo apt install openjdk-21-jdk maven
```

### 2. Configuration Production

#### Variables d'Environnement

```bash
# Créer un fichier .env
cat > .env << EOF
# Base de données
DB_HOST=production-db-host
DB_PORT=3306
DB_NAME=cardmanager_prod
DB_USER=cardmanager_user
DB_PASSWORD=super_secure_password

# Sécurité
SPRING_PROFILES_ACTIVE=production
PAINTER_SECURITY_LOGIN_ENABLED=true
RETRIEVER_SECURITY_LOGIN_ENABLED=true

# Stockage
PAINTER_IMAGE_STORAGE_PATH=/data/images
RETRIEVER_UPLOADS_PATH=/data/uploads

# Logs
LOG_LEVEL=INFO
EOF
```

#### Docker Compose Production

```yaml
# docker-compose.prod.yml
version: '3.8'

services:
  painter:
    build:
      context: .
      dockerfile: painter/Dockerfile.simple
    container_name: cardmanager-painter-prod
    restart: unless-stopped
    environment:
      SPRING_PROFILES_ACTIVE: production
      SPRING_DATASOURCE_URL: jdbc:mariadb://${DB_HOST}:${DB_PORT}/${DB_NAME}
      SPRING_DATASOURCE_USERNAME: ${DB_USER}
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      PAINTER_SECURITY_LOGIN_ENABLED: true
    volumes:
      - /data/cardmanager/images:/app/images/storage
      - /var/log/cardmanager:/app/logs
    networks:
      - cardmanager-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

  retriever:
    build:
      context: .
      dockerfile: gestioncarte/Dockerfile.simple
    container_name: cardmanager-retriever-prod
    restart: unless-stopped
    environment:
      SPRING_PROFILES_ACTIVE: production
      SPRING_DATASOURCE_URL: jdbc:mariadb://${DB_HOST}:${DB_PORT}/${DB_NAME}
      SPRING_DATASOURCE_USERNAME: ${DB_USER}
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      RETRIEVER_SECURITY_LOGIN_ENABLED: true
    volumes:
      - /data/cardmanager/uploads:/app/uploads
      - /var/log/cardmanager:/app/logs
    ports:
      - "8080:8080"
    depends_on:
      - painter
    networks:
      - cardmanager-network

  image-server:
    image: nginx:alpine
    container_name: cardmanager-images-prod
    restart: unless-stopped
    ports:
      - "8083:80"
    volumes:
      - /data/cardmanager/images:/usr/share/nginx/html/images:ro
      - ./nginx-images.conf:/etc/nginx/conf.d/default.conf:ro
      - ./image-viewer.html:/usr/share/nginx/html/image-viewer.html:ro
    networks:
      - cardmanager-network

networks:
  cardmanager-network:
    driver: bridge

volumes:
  cardmanager_data:
    driver: local
```

### 3. Sécurisation

#### Reverse Proxy (Nginx)

```nginx
# /etc/nginx/sites-available/cardmanager
server {
    listen 80;
    server_name cardmanager.votre-domaine.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name cardmanager.votre-domaine.com;

    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;

    # Application principale
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Visionneuse d'images
    location /images/ {
        proxy_pass http://localhost:8083/;
        proxy_set_header Host $host;
    }
}
```

#### Firewall

```bash
# UFW (Ubuntu)
sudo ufw allow ssh
sudo ufw allow 80
sudo ufw allow 443
sudo ufw deny 8080  # Bloquer accès direct
sudo ufw deny 8081
sudo ufw deny 8083
sudo ufw enable
```

### 4. Démarrage Production

```bash
# Créer les répertoires de données
sudo mkdir -p /data/cardmanager/{images,uploads}
sudo mkdir -p /var/log/cardmanager
sudo chown -R 999:999 /data/cardmanager  # UID docker

# Build et démarrage
mvn clean package -DskipTests -Pprod
docker-compose -f docker-compose.prod.yml up -d

# Vérification
docker-compose -f docker-compose.prod.yml ps
docker-compose -f docker-compose.prod.yml logs
```

## 🗄️ Configuration Base de Données

### Optimisation MariaDB/MySQL

```sql
-- Configuration recommandée pour production
[mysqld]
innodb_buffer_pool_size = 2G
innodb_log_file_size = 512M
innodb_flush_log_at_trx_commit = 2
innodb_flush_method = O_DIRECT
query_cache_size = 256M
query_cache_type = 1
max_connections = 200
thread_cache_size = 50

-- Index recommandés
USE cardmanager_prod;

-- Index sur les tables principales
CREATE INDEX idx_card_name ON card(name);
CREATE INDEX idx_image_path ON image(path);
CREATE INDEX idx_card_image_card_id ON card_image(card_id);
CREATE INDEX idx_set_image_set_id ON set_image(set_id);
```

### Migration de Données

```bash
# Backup base existante
mysqldump -u ia -pfoufafou dev > backup_dev.sql

# Restauration en production
mysql -u cardmanager_user -p cardmanager_prod < backup_dev.sql

# Mise à jour des chemins d'images si nécessaire
UPDATE image SET path = REPLACE(path, '/old/path/', '/app/images/storage/');
```

## 🔒 Sécurité

### SSL/TLS

```bash
# Générer certificat Let's Encrypt
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d cardmanager.votre-domaine.com
```

### Authentification

```yaml
# Configuration Spring Security
spring:
  security:
    user:
      name: admin
      password: ${ADMIN_PASSWORD:changeme}
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
```

### Secrets Management

```bash
# Utiliser Docker Secrets
echo "super_secure_password" | docker secret create db_password -
echo "jwt_secret_key" | docker secret create jwt_secret -
```

## 📊 Monitoring

### Health Checks

```bash
# Script de monitoring
#!/bin/bash
# monitor.sh

ENDPOINTS=(
    "http://localhost:8080/actuator/health"
    "http://localhost:8081/actuator/health"
    "http://localhost:8083/images/"
)

for endpoint in "${ENDPOINTS[@]}"; do
    if curl -f "$endpoint" >/dev/null 2>&1; then
        echo "✅ $endpoint OK"
    else
        echo "❌ $endpoint FAILED"
        # Alerting logic here
    fi
done
```

### Logs

```bash
# Centralisation des logs
docker-compose -f docker-compose.prod.yml logs -f | tee /var/log/cardmanager/app.log

# Rotation des logs
cat > /etc/logrotate.d/cardmanager << EOF
/var/log/cardmanager/*.log {
    daily
    rotate 30
    compress
    delaycompress
    create 644 root root
    postrotate
        docker-compose -f /opt/cardmanager/docker-compose.prod.yml restart
    endscript
}
EOF
```

### Métriques

```bash
# Prometheus endpoint
curl http://localhost:8080/actuator/prometheus

# Grafana dashboard
# Importer le dashboard CardManager depuis grafana.com
```

## 🔧 Maintenance

### Backup Automatique

```bash
#!/bin/bash
# backup.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backup/cardmanager"

# Backup base de données
mysqldump -u cardmanager_user -p${DB_PASSWORD} cardmanager_prod > "$BACKUP_DIR/db_$DATE.sql"

# Backup images
tar -czf "$BACKUP_DIR/images_$DATE.tar.gz" /data/cardmanager/images/

# Nettoyage anciens backups (garder 30 jours)
find "$BACKUP_DIR" -name "*.sql" -mtime +30 -delete
find "$BACKUP_DIR" -name "*.tar.gz" -mtime +30 -delete
```

### Mise à jour

```bash
# Processus de mise à jour zero-downtime
#!/bin/bash
# update.sh

# 1. Backup
./backup.sh

# 2. Pull nouveau code
git pull origin main

# 3. Build nouvelle version
mvn clean package -DskipTests

# 4. Rolling update
docker-compose -f docker-compose.prod.yml up -d --no-deps painter
sleep 30
docker-compose -f docker-compose.prod.yml up -d --no-deps retriever

# 5. Vérification
./monitor.sh
```

## 🆘 Dépannage

### Problèmes Courants

#### Services ne démarrent pas

```bash
# Diagnostic complet
docker-compose -f docker-compose.prod.yml ps
docker-compose -f docker-compose.prod.yml logs painter
docker system df
docker system prune -f
```

#### Performance dégradée

```bash
# Monitoring ressources
docker stats
htop
iotop

# Analyse logs
tail -f /var/log/cardmanager/app.log | grep ERROR
```

#### Base de données lente

```sql
-- Diagnostic MySQL
SHOW PROCESSLIST;
SHOW ENGINE INNODB STATUS;
SELECT * FROM INFORMATION_SCHEMA.INNODB_METRICS WHERE status = 'enabled';

-- Optimisation
OPTIMIZE TABLE card, image, card_image;
ANALYZE TABLE card, image, card_image;
```

### Contacts Support

- 🐛 **Issues GitHub** : [Créer un ticket](https://github.com/votre-username/cardmanager/issues)
- 📧 **Email** : support@cardmanager.dev
- 💬 **Discord** : [Serveur Communauté](https://discord.gg/cardmanager)

---

## 📚 Ressources Complémentaires

- [Architecture Guide](docs/ARCHITECTURE.md)
- [API Documentation](docs/API.md)
- [Performance Tuning](docs/PERFORMANCE.md)
- [Security Best Practices](docs/SECURITY.md)

---

<div align="center">
<strong>🎮 Happy Deploying! 🚀</strong>
</div>