-- À placer dans docker/init.sql
-- Script d'initialisation de la base de données

-- Création de la base si elle n'existe pas
CREATE DATABASE IF NOT EXISTS cardmanager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Utiliser la base
USE cardmanager;

-- Créer l'utilisateur applicatif si nécessaire
CREATE USER IF NOT EXISTS 'carduser'@'%' IDENTIFIED BY 'cardpassword';
GRANT ALL PRIVILEGES ON cardmanager.* TO 'carduser'@'%';
FLUSH PRIVILEGES;

-- Configuration pour améliorer les performances
SET GLOBAL innodb_buffer_pool_size = 256M;
SET GLOBAL max_connections = 200;

-- Afficher la version pour confirmation
SELECT 'Database initialized successfully' AS status, VERSION() AS version;