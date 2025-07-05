#!/bin/bash
# fix-encoding.sh - Corriger les problèmes d'encodage

echo "🔧 Correction des fichiers d'encodage..."

# Supprimer le fichier problématique s'il existe
if [ -f "painter/painter/src/main/resources/application-docker.properties" ]; then
    echo "🗑️ Suppression de l'ancien fichier application-docker.properties"
    rm "painter/painter/src/main/resources/application-docker.properties"
fi

# Créer le nouveau fichier avec le bon encodage
echo "📝 Création du nouveau fichier application-docker.properties pour Painter"
cat > "painter/painter/src/main/resources/application-docker.properties" << 'EOF'
# Configuration Painter pour Docker
server.port=8081

# Base de donnees
spring.datasource.url=jdbc:mariadb://database:3306/cardmanager?useUnicode=yes&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&noAccessToProcedureBodies=true
spring.datasource.username=carduser
spring.datasource.password=cardpassword
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect

# Liquibase
spring.liquibase.enabled=false

# Securite
painter.security.login.enabled=false

# Stockage des images
painter.image.storage-path=/app/images/storage/
painter.image.legacy-storage-path=/app/images/legacy/

# Logs
logging.level.com.pcagrade=INFO
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n

# Actuator
management.endpoint.health.enabled=true
management.endpoints.web.exposure.include=health,info
management.health.db.enabled=true

# Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
EOF

# Faire de même pour retriever si nécessaire
if [ ! -f "gestioncarte/src/main/resources/application-docker.properties" ]; then
    echo "📝 Création du fichier application-docker.properties pour Retriever"
    cat > "gestioncarte/src/main/resources/application-docker.properties" << 'EOF'
# Configuration pour l'environnement Docker
spring.profiles.active=docker

# Base de donnees
spring.datasource.url=jdbc:mariadb://database:3306/cardmanager?useUnicode=yes&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&noAccessToProcedureBodies=true
spring.datasource.username=carduser
spring.datasource.password=cardpassword
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# Configuration JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect

# Liquibase
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml

# Logs
logging.level.com.pcagrade=INFO
logging.level.org.hibernate.SQL=WARN
logging.file.name=/app/logs/application.log
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n

# Securite
retriever.security.login.enabled=false

# Chemins des ressources
retriever.common-resource.path=/app/uploads/
retriever.cache.page-cache.path=/app/cache/pages/

# Configuration Actuator
management.endpoint.health.enabled=true
management.endpoint.health.show-details=always
management.endpoints.web.exposure.include=health,info,metrics
management.health.db.enabled=true
management.health.diskspace.enabled=true

# Configuration du serveur
server.port=8080
server.tomcat.connection-timeout=30000
server.forward-headers-strategy=framework
EOF
fi

echo "✅ Fichiers corrigés avec le bon encodage"
echo "Vous pouvez maintenant relancer le build Maven"