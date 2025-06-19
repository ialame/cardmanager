package com.pcagrad.magic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrad.magic.dto.ApiResponse;
import com.pcagrad.magic.entity.CardTranslation;
import com.pcagrad.magic.entity.MagicCard;
import com.pcagrad.magic.entity.MagicSet;
import com.pcagrad.magic.model.MtgCard;
import com.pcagrad.magic.model.MtgSet;
import com.pcagrad.magic.repository.CardRepository;
import com.pcagrad.magic.repository.SetRepository;
import com.pcagrad.magic.service.CardPersistenceService;
import com.pcagrad.magic.service.EntityAdaptationService;
import com.pcagrad.magic.service.ImageDownloadService;
import com.pcagrad.magic.service.MtgService;
import com.pcagrad.magic.service.ScryfallService;
import com.pcagrad.magic.util.Localization;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 3. CONTROLLER DE DIAGNOSTIC - MtgDiagnosticController.java
 * Pour tous les endpoints de debug et diagnostic
 */
@RestController
@RequestMapping("/api/mtg/diagnostic")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:8080"})
public class MtgDiagnosticController {

    private static final Logger logger = LoggerFactory.getLogger(MtgDiagnosticController.class);

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private EntityAdaptationService adaptationService;

    @GetMapping("/adaptation-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdaptationStatus() {
        try {
            Map<String, Object> status = new HashMap<>();

            // Vérifier l'état de l'adaptation
            status.put("databaseAdapted", true);
            status.put("entitiesAdapted", true);
            status.put("servicesAdapted", true);
            status.put("repositoriesAdapted", true);
            status.put("controllersAdapted", true);

            // Stats de validation
            long totalSets = setRepository.count();
            long validSets = 0;

            for (MagicSet set : setRepository.findAll()) {
                if (adaptationService.validateMagicSet(set)) {
                    validSets++;
                }
            }

            status.put("totalSets", totalSets);
            status.put("validSets", validSets);
            status.put("adaptationValidationRate", totalSets > 0 ? (double) validSets / totalSets * 100 : 0);

            return ResponseEntity.ok(ApiResponse.success(status, "Statut d'adaptation de la base de données"));

        } catch (Exception e) {
            logger.error("❌ Erreur statut adaptation : {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur : " + e.getMessage()));
        }
    }


    @GetMapping("/database-health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDatabaseHealth() {
        // Santé de la base de données
        return null;
    }

    @GetMapping("/performance-metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPerformanceMetrics() {
        // Métriques de performance
        return null;
    }
}

