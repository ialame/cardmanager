package com.pcagrad.magic.controller;

import com.pcagrad.magic.controller.utils.MtgControllerUtils;
import com.pcagrad.magic.dto.ApiResponse;
import com.pcagrad.magic.entity.MagicCard;
import com.pcagrad.magic.entity.MagicSet;
import com.pcagrad.magic.model.MtgCard;
import com.pcagrad.magic.model.MtgSet;
import com.pcagrad.magic.repository.CardRepository;
import com.pcagrad.magic.repository.SetRepository;
import com.pcagrad.magic.service.MtgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller principal MTG - API publique
 * Contient uniquement les endpoints essentiels pour l'API publique
 * Les endpoints d'administration sont dans MtgAdminController
 */
@RestController
@RequestMapping("/api/mtg")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:8080"})
@Transactional(readOnly = true)
public class MtgController {

    private static final Logger logger = LoggerFactory.getLogger(MtgController.class);

    @Autowired
    private MtgService mtgService;

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private MtgControllerUtils controllerUtils;

    // ========== ENDPOINTS PUBLICS ESSENTIELS ==========

    /**
     * Récupère toutes les extensions disponibles
     */
    @GetMapping("/sets")
    public Mono<ResponseEntity<ApiResponse<List<MtgSet>>>> getAllSets() {
        controllerUtils.logOperation("Récupération extensions", "Demande de toutes les extensions");

        return mtgService.getAllSets()
                .map(sets -> {
                    controllerUtils.logOperationResult("Récupération extensions", true,
                            sets.size() + " extensions trouvées");
                    return ResponseEntity.ok(ApiResponse.success(sets, "Extensions récupérées"));
                })
                .onErrorReturn(ResponseEntity.badRequest()
                        .body(ApiResponse.error("Erreur lors de la récupération des extensions")));
    }

    /**
     * Récupère la dernière extension disponible
     */
    @GetMapping("/sets/latest")
    public Mono<ResponseEntity<ApiResponse<MtgSet>>> getLatestSet() {
        controllerUtils.logOperation("Récupération dernière extension", "Demande de la dernière extension");

        return mtgService.getLatestSet()
                .map(set -> {
                    if (set != null) {
                        controllerUtils.logOperationResult("Récupération dernière extension", true,
                                "Extension " + set.code() + " trouvée");
                        return ResponseEntity.ok(ApiResponse.success(set, "Dernière extension récupérée"));
                    } else {
                        controllerUtils.logOperationResult("Récupération dernière extension", false,
                                "Aucune extension trouvée");
                        return ResponseEntity.notFound().<ApiResponse<MtgSet>>build();
                    }
                })
                .onErrorReturn(ResponseEntity.badRequest()
                        .body(ApiResponse.error("Erreur lors de la récupération de la dernière extension")));
    }

    /**
     * Récupère la dernière extension avec ses cartes
     */
    @GetMapping("/sets/latest/cards")
    public Mono<ResponseEntity<ApiResponse<MtgSet>>> getLatestSetWithCards() {
        controllerUtils.logOperation("Récupération dernière extension avec cartes",
                "Demande de la dernière extension avec cartes");

        return mtgService.getLatestSetWithCards()
                .map(set -> {
                    if (set != null) {
                        int cardCount = set.cards() != null ? set.cards().size() : 0;
                        controllerUtils.logOperationResult("Récupération dernière extension avec cartes", true,
                                String.format("Extension %s avec %d cartes", set.code(), cardCount));
                        return ResponseEntity.ok(ApiResponse.success(set,
                                "Dernière extension avec cartes récupérée"));
                    } else {
                        controllerUtils.logOperationResult("Récupération dernière extension avec cartes", false,
                                "Aucune extension trouvée");
                        return ResponseEntity.notFound().<ApiResponse<MtgSet>>build();
                    }
                })
                .onErrorReturn(ResponseEntity.badRequest()
                        .body(ApiResponse.error("Erreur lors de la récupération de la dernière extension avec cartes")));
    }

    /**
     * Récupère les cartes d'une extension spécifique
     */
    @GetMapping("/sets/{setCode}/cards")
    public Mono<ResponseEntity<ApiResponse<List<MtgCard>>>> getCardsFromSet(@PathVariable String setCode) {
        if (!controllerUtils.isValidSetCode(setCode)) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(ApiResponse.error("Code d'extension invalide")));
        }

        String cleanSetCode = controllerUtils.cleanSetCode(setCode);
        controllerUtils.logOperation("Récupération cartes", "Extension " + cleanSetCode);

        return mtgService.getCardsFromSet(cleanSetCode)
                .map(cards -> {
                    controllerUtils.logOperationResult("Récupération cartes", true,
                            cards.size() + " cartes trouvées pour " + cleanSetCode);
                    return ResponseEntity.ok(ApiResponse.success(cards,
                            "Cartes de l'extension " + cleanSetCode + " récupérées"));
                })
                .onErrorReturn(ResponseEntity.badRequest()
                        .body(ApiResponse.error("Erreur lors de la récupération des cartes de l'extension " + cleanSetCode)));
    }

    /**
     * Récupère une extension avec ses cartes (depuis la base de données)
     */
    @GetMapping("/sets/{setCode}/with-cards")
    public ResponseEntity<ApiResponse<Object>> getSetWithCards(@PathVariable String setCode) {
        if (!controllerUtils.isValidSetCode(setCode)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Code d'extension invalide"));
        }

        try {
            String cleanSetCode = controllerUtils.cleanSetCode(setCode);
            controllerUtils.logOperation("Récupération extension avec cartes", "Extension " + cleanSetCode);

            Optional<MagicSet> setEntity = setRepository.findByCode(cleanSetCode);
            if (setEntity.isEmpty()) {
                controllerUtils.logOperationResult("Récupération extension avec cartes", false,
                        "Extension " + cleanSetCode + " non trouvée");
                return ResponseEntity.notFound().build();
            }

            List<MagicCard> cards = cardRepository.findBySetCodeOrderByNameAsc(cleanSetCode);

            Map<String, Object> response = new HashMap<>();
            response.put("set", setEntity.get());
            response.put("cards", cards.stream()
                    .map(controllerUtils::convertCardToMap)
                    .collect(Collectors.toList()));
            response.put("cardCount", cards.size());

            controllerUtils.logOperationResult("Récupération extension avec cartes", true,
                    String.format("Extension %s avec %d cartes", setEntity.get().getName(), cards.size()));

            return ResponseEntity.ok(ApiResponse.success(response,
                    String.format("Extension %s avec %d cartes", setEntity.get().getName(), cards.size())));

        } catch (Exception e) {
            logger.error("❌ Erreur lors de la récupération de l'extension {} : {}", setCode, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Erreur lors de la récupération de l'extension : " + e.getMessage()));
        }
    }

    /**
     * Récupère une extension spécifique par son code
     */
    @GetMapping("/sets/{setCode}")
    public ResponseEntity<ApiResponse<MagicSet>> getSetByCode(@PathVariable String setCode) {
        if (!controllerUtils.isValidSetCode(setCode)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Code d'extension invalide"));
        }

        try {
            String cleanSetCode = controllerUtils.cleanSetCode(setCode);
            controllerUtils.logOperation("Récupération extension", "Extension " + cleanSetCode);

            Optional<MagicSet> setEntity = setRepository.findByCode(cleanSetCode);
            if (setEntity.isEmpty()) {
                controllerUtils.logOperationResult("Récupération extension", false,
                        "Extension " + cleanSetCode + " non trouvée");
                return ResponseEntity.notFound().build();
            }

            controllerUtils.logOperationResult("Récupération extension", true,
                    "Extension " + setEntity.get().getName() + " trouvée");

            return ResponseEntity.ok(ApiResponse.success(setEntity.get(),
                    "Extension " + cleanSetCode + " récupérée"));

        } catch (Exception e) {
            logger.error("❌ Erreur lors de la récupération de l'extension {} : {}", setCode, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Erreur lors de la récupération de l'extension : " + e.getMessage()));
        }
    }

    // ========== ENDPOINTS DE SANTÉ ET STATUS ==========

    /**
     * Endpoint de santé pour vérifier que l'API fonctionne
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        try {
            Map<String, Object> health = new HashMap<>();
            health.put("status", "UP");
            health.put("timestamp", System.currentTimeMillis());
            health.put("version", "1.0.0");

            // Vérifications rapides
            long totalSets = setRepository.count();
            long totalCards = cardRepository.count();

            health.put("totalSets", totalSets);
            health.put("totalCards", totalCards);
            health.put("database", totalSets >= 0 ? "UP" : "DOWN");

            return ResponseEntity.ok(ApiResponse.success(health, "API MTG opérationnelle"));

        } catch (Exception e) {
            Map<String, Object> health = new HashMap<>();
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            health.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error("Service temporairement indisponible"));
        }
    }

    /**
     * Informations sur l'API
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "MTG Cards API");
        info.put("version", "1.0.0");
        info.put("description", "API pour la gestion des cartes Magic: The Gathering");
        info.put("endpoints", Map.of(
                "sets", "/api/mtg/sets",
                "latest", "/api/mtg/sets/latest",
                "cards", "/api/mtg/sets/{setCode}/cards",
                "admin", "/api/mtg/admin/*",
                "health", "/api/mtg/health"
        ));

        return ResponseEntity.ok(ApiResponse.success(info, "Informations API MTG"));
    }
}