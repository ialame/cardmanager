package com.pcagrad.magic.controller;

import com.pcagrad.magic.dto.ApiResponse;
import com.pcagrad.magic.entity.MagicCard;
import com.pcagrad.magic.repository.CardRepository;
import com.pcagrad.magic.service.CardPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 4. CONTROLLER SPÉCIALISÉ CARTES - MtgCardController.java
 * Pour les opérations spécifiques aux cartes
 */
@RestController
@RequestMapping("/api/mtg/cards")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:8080"})
public class MtgCardController {

    private static final Logger logger = LoggerFactory.getLogger(MtgCardController.class);

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardPersistenceService cardPersistenceService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MagicCard>>> searchCards(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String setCode,
            @RequestParam(required = false) String rarity) {
        // Recherche de cartes avec filtres
        return null;
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<ApiResponse<MagicCard>> getCardById(@PathVariable UUID cardId) {
        // Récupérer une carte par ID
        return null;
    }

    @GetMapping("/random")
    public ResponseEntity<ApiResponse<MagicCard>> getRandomCard() {
        // Carte aléatoire
        return null;
    }

    // ===== AJOUTEZ SEULEMENT cet endpoint dans un de vos controllers existants =====

    /**
     * Endpoint simple pour réparer toutes les relations manquantes
     * AJOUTEZ SEULEMENT cette méthode dans MtgController.java ou MtgAdminController.java
     */
    @PostMapping("/admin/repair-all-relations")
    public ResponseEntity<ApiResponse<Map<String, Object>>> repairAllRelations() {
        try {
            int repairedCount = cardPersistenceService.repairAllMissingRelations();

            Map<String, Object> result = new HashMap<>();
            result.put("repairedRelations", repairedCount);
            result.put("success", true);
            result.put("message", "Toutes les relations manquantes ont été réparées");

            return ResponseEntity.ok(ApiResponse.success(result,
                    String.format("✅ %d relations réparées", repairedCount)));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur réparation : " + e.getMessage()));
        }
    }
}

