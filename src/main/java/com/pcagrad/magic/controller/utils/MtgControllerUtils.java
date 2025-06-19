package com.pcagrad.magic.controller.utils;

import com.pcagrad.magic.dto.ApiResponse;
import com.pcagrad.magic.entity.MagicCard;
import com.pcagrad.magic.util.Localization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilitaire commune pour tous les controllers MTG
 * Contient les méthodes partagées pour éviter la duplication de code
 */
@Component
public class MtgControllerUtils {

    private static final Logger logger = LoggerFactory.getLogger(MtgControllerUtils.class);

    /**
     * Convertit une MagicCard en Map pour les réponses API
     * Version simple pour les listes
     */
    public Map<String, Object> convertCardToMap(MagicCard card) {
        Map<String, Object> cardMap = new HashMap<>();

        try {
            cardMap.put("id", card.getId());
            cardMap.put("numero", card.getNumero());
            cardMap.put("idPrim", card.getIdPrim());
            cardMap.put("setCode", card.getZPostExtension());

            // Nom depuis la traduction
            if (card.getTranslation(Localization.USA) != null) {
                cardMap.put("name", card.getTranslation(Localization.USA).getName());
            } else {
                cardMap.put("name", "Nom non disponible");
            }

            // Propriétés de base
            cardMap.put("isAffichable", card.getIsAffichable());
            cardMap.put("hasRecherche", card.getHasRecherche());
            cardMap.put("certifiable", card.getCertifiable());
            cardMap.put("hasImg", card.getHasImg());

            return cardMap;

        } catch (Exception e) {
            logger.warn("⚠️ Erreur conversion carte {} : {}", card.getId(), e.getMessage());
            return createErrorCardMap(card.getId());
        }
    }

    /**
     * Convertit une MagicCard en Map détaillée pour les réponses API complètes
     */
    public Map<String, Object> convertCardToDetailedMap(MagicCard card) {
        Map<String, Object> cardMap = convertCardToMap(card);

        try {
            // Ajouter les colonnes physiques
            cardMap.put("rarity", card.getRarity());
            cardMap.put("layout", card.getLayout());
            cardMap.put("colors", card.getColors());
            cardMap.put("colorIdentity", card.getColorIdentity());
            cardMap.put("types", card.getTypes());

            // Ajouter les propriétés booléennes
            cardMap.put("hasFoil", card.getHasFoil());
            cardMap.put("hasNonFoil", card.getHasNonFoil());
            cardMap.put("isFoilOnly", card.getIsFoilOnly());
            cardMap.put("isOnlineOnly", card.getIsOnlineOnly());
            cardMap.put("isOversized", card.getIsOversized());
            cardMap.put("isTimeshifted", card.getIsTimeshifted());
            cardMap.put("isToken", card.getIsToken());

            // Ajouter les données JSON si disponibles
            if (card.getAttributes() != null && !card.getAttributes().equals("{}")) {
                cardMap.put("hasAttributes", true);
                cardMap.put("attributesSize", card.getAttributes().length());
            } else {
                cardMap.put("hasAttributes", false);
            }

            if (card.getAllowedNotes() != null && !card.getAllowedNotes().equals("[]")) {
                cardMap.put("hasAllowedNotes", true);
                cardMap.put("allowedNotesSize", card.getAllowedNotes().length());
            } else {
                cardMap.put("hasAllowedNotes", false);
            }

            return cardMap;

        } catch (Exception e) {
            logger.error("❌ Erreur conversion détaillée carte {} : {}", card.getId(), e.getMessage());
            return cardMap; // Retourner au moins la version simple
        }
    }

    /**
     * Crée une map d'erreur pour une carte
     */
    private Map<String, Object> createErrorCardMap(Object cardId) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("id", cardId);
        errorMap.put("name", "Erreur de chargement");
        errorMap.put("error", true);
        return errorMap;
    }

    /**
     * Gestion centralisée des erreurs pour les controllers
     */
    public ResponseEntity<ApiResponse<Object>> handleError(String operation, Exception e) {
        logger.error("❌ Erreur lors de {} : {}", operation, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur lors de " + operation + " : " + e.getMessage()));
    }

    /**
     * Gestion des erreurs avec code HTTP personnalisé
     */
    public ResponseEntity<ApiResponse<Object>> handleError(String operation, Exception e, HttpStatus status) {
        logger.error("❌ Erreur lors de {} : {}", operation, e.getMessage());
        return ResponseEntity.status(status)
                .body(ApiResponse.error("Erreur lors de " + operation + " : " + e.getMessage()));
    }

    /**
     * Log standardisé pour les opérations
     */
    public void logOperation(String operation, String details) {
        logger.info("🔍 {} : {}", operation, details);
    }

    /**
     * Log standardisé pour les opérations avec résultat
     */
    public void logOperationResult(String operation, boolean success, String details) {
        if (success) {
            logger.info("✅ {} réussi : {}", operation, details);
        } else {
            logger.warn("⚠️ {} échoué : {}", operation, details);
        }
    }

    /**
     * Crée une réponse de succès standardisée
     */
    public ResponseEntity<ApiResponse<Map<String, Object>>> createSuccessResponse(
            Map<String, Object> data, String message) {
        return ResponseEntity.ok(ApiResponse.success(data, message));
    }

    /**
     * Crée une réponse d'erreur standardisée
     */
    public ResponseEntity<ApiResponse<Object>> createErrorResponse(String message) {
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }

    /**
     * Crée une réponse d'erreur avec code HTTP personnalisé
     */
    public ResponseEntity<ApiResponse<Object>> createErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(ApiResponse.error(message));
    }

    /**
     * Valide les paramètres de base pour les endpoints
     */
    public boolean isValidSetCode(String setCode) {
        return setCode != null && !setCode.trim().isEmpty() && setCode.length() >= 2 && setCode.length() <= 10;
    }

    /**
     * Valide qu'un paramètre n'est pas null ou vide
     */
    public boolean isValidParameter(String parameter) {
        return parameter != null && !parameter.trim().isEmpty();
    }

    /**
     * Crée des statistiques standardisées pour les réponses
     */
    public Map<String, Object> createStatsMap(String operation, long total, long processed, long errors) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("operation", operation);
        stats.put("total", total);
        stats.put("processed", processed);
        stats.put("errors", errors);
        stats.put("successRate", total > 0 ? Math.round((double) (processed - errors) / total * 100) : 0);
        stats.put("timestamp", System.currentTimeMillis());
        return stats;
    }

    /**
     * Formate un message de résultat standardisé
     */
    public String formatResultMessage(String operation, long total, long processed, long errors) {
        if (errors == 0) {
            return String.format("%s terminé : %d/%d éléments traités avec succès",
                    operation, processed, total);
        } else {
            return String.format("%s terminé : %d/%d éléments traités (%d erreurs)",
                    operation, processed - errors, total, errors);
        }
    }

    /**
     * Vérifie si une extension existe et est valide
     */
    public boolean isValidSetResponse(Map<String, Object> setData) {
        return setData != null &&
                setData.containsKey("code") &&
                setData.containsKey("name") &&
                setData.get("code") != null &&
                setData.get("name") != null;
    }

    /**
     * Nettoie et valide un code d'extension
     */
    public String cleanSetCode(String setCode) {
        if (setCode == null) return null;
        return setCode.trim().toUpperCase();
    }

    /**
     * Crée un résumé des modifications pour les logs
     */
    public String createChangesSummary(int created, int updated, int deleted, int errors) {
        StringBuilder summary = new StringBuilder();
        if (created > 0) summary.append(created).append(" créés ");
        if (updated > 0) summary.append(updated).append(" mis à jour ");
        if (deleted > 0) summary.append(deleted).append(" supprimés ");
        if (errors > 0) summary.append(errors).append(" erreurs");

        return summary.length() > 0 ? summary.toString().trim() : "Aucune modification";
    }
}