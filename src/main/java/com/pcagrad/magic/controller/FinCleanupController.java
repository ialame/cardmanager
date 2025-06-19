package com.pcagrad.magic.controller;

import com.pcagrad.magic.dto.ApiResponse;
import com.pcagrad.magic.service.FinCleanupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/fin")
public class FinCleanupController {

    private static final Logger logger = LoggerFactory.getLogger(FinCleanupController.class);

    @Autowired
    private FinCleanupService finCleanupService;

    /**
     * Statistiques de l'extension FIN
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFinStats() {
        try {
            Map<String, Object> stats = finCleanupService.getFinStatistics();
            return ResponseEntity.ok(ApiResponse.success(stats, "Statistiques FIN"));
        } catch (Exception e) {
            logger.error("Erreur récupération stats FIN", e);
            return ResponseEntity.ok(ApiResponse.error("Erreur stats FIN: " + e.getMessage()));
        }
    }

    /**
     * Nettoyage complet de l'extension FIN
     */
    @DeleteMapping("/cleanup-complete")
    public ResponseEntity<ApiResponse<Map<String, Object>>> cleanupComplete() {
        try {
            logger.info("🧹 Demande de nettoyage complet FIN");
            Map<String, Object> result = finCleanupService.cleanupFinExtensionCompletely();

            if ((Boolean) result.getOrDefault("success", false)) {
                return ResponseEntity.ok(ApiResponse.success(result, "Nettoyage FIN terminé"));
            } else {
                return ResponseEntity.ok(ApiResponse.error("Échec nettoyage FIN: " + result.get("error")));
            }
        } catch (Exception e) {
            logger.error("Erreur nettoyage FIN", e);
            return ResponseEntity.ok(ApiResponse.error("Erreur nettoyage FIN: " + e.getMessage()));
        }
    }

    /**
     * Nettoyage sécurisé avec vérifications
     */
    @DeleteMapping("/cleanup-safe")
    public ResponseEntity<ApiResponse<Map<String, Object>>> cleanupSafe() {
        try {
            logger.info("🔒 Demande de nettoyage sécurisé FIN");
            Map<String, Object> result = finCleanupService.safeCleanupFinExtension();

            if ((Boolean) result.getOrDefault("success", false)) {
                return ResponseEntity.ok(ApiResponse.success(result, "Nettoyage sécurisé FIN terminé"));
            } else {
                return ResponseEntity.ok(ApiResponse.error("Échec nettoyage sécurisé FIN: " + result.get("error")));
            }
        } catch (Exception e) {
            logger.error("Erreur nettoyage sécurisé FIN", e);
            return ResponseEntity.ok(ApiResponse.error("Erreur nettoyage sécurisé FIN: " + e.getMessage()));
        }
    }
}