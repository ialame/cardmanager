package com.pcagrad.magic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrad.magic.dto.ApiResponse;
import com.pcagrad.magic.entity.CardTranslation;
import com.pcagrad.magic.entity.MagicCard;
import com.pcagrad.magic.entity.MagicCardTranslation;
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
import com.pcagrad.magic.util.UlidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller dédié à l'administration des extensions et cartes MTG
 * Contient tous les endpoints /admin/* pour une meilleure organisation
 */
@RestController
@RequestMapping("/api/mtg/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:8080"})
public class MtgAdminController {

    private static final Logger logger = LoggerFactory.getLogger(MtgAdminController.class);

    @Autowired
    private MtgService mtgService;

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ImageDownloadService imageDownloadService;

    @Autowired
    private ScryfallService scryfallService;

    @Autowired
    private CardPersistenceService persistenceService;

    @Autowired
    private EntityAdaptationService adaptationService;

    // ========== INITIALISATION ET CONFIGURATION ==========

    /**
     * Initialise l'application avec Final Fantasy comme extension de test
     */
    @PostMapping("/initialize-with-fin")
    public ResponseEntity<ApiResponse<Map<String, Object>>> initializeWithFin() {
        try {
            logger.info("🚀 Initialisation de l'application avec Final Fantasy");

            Map<String, Object> result = new HashMap<>();

            Optional<MagicSet> finSet = setRepository.findByCode("FIN");
            if (finSet.isEmpty()) {
                MagicSet fin = new MagicSet();
                fin.setCode("FIN");
                fin.setName("Magic: The Gathering - FINAL FANTASY");
                fin.setReleaseDate(LocalDate.now());

                adaptationService.setMagicSetType(fin, "expansion");
                adaptationService.prepareMagicSetForSave(fin, "expansion");

                finSet = Optional.of(setRepository.save(fin));
                result.put("finCreated", true);
                logger.info("✅ Extension Final Fantasy créée");
            } else {
                result.put("finCreated", false);
                logger.info("✅ Extension Final Fantasy existante");
            }

            long cardCount = cardRepository.countBySetCode("FIN");
            result.put("finCardCount", cardCount);

            mtgService.forceFinalFantasyAsLatest();
            result.put("finSetAsLatest", true);

            // Test de récupération
            try {
                MtgSet latestSet = mtgService.getLatestSet().block();
                result.put("latestSetCode", latestSet != null ? latestSet.code() : "NONE");
                result.put("latestSetName", latestSet != null ? latestSet.name() : "NONE");
                result.put("initializationSuccess", true);
            } catch (Exception e) {
                logger.warn("⚠️ Problème lors du test de récupération : {}", e.getMessage());
                result.put("initializationSuccess", false);
                result.put("testError", e.getMessage());
            }

            return ResponseEntity.ok(ApiResponse.success(result, "Initialisation terminée"));

        } catch (Exception e) {
            logger.error("❌ Erreur lors de l'initialisation : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Erreur lors de l'initialisation : " + e.getMessage()));
        }
    }

    // ========== SAUVEGARDE DES CARTES ==========

    /**
     * Sauvegarde Final Fantasy avec colonnes physiques (solution propre)
     */
    @PostMapping("/save-final-fantasy-clean")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> saveFinalFantasyClean() {
        try {
            logger.info("🚀 === SAUVEGARDE FINAL FANTASY PROPRE ===");

            // Récupérer les cartes Final Fantasy
            Mono<List<MtgCard>> cardsMono = mtgService.getCardsFromSet("FIN");
            List<MtgCard> finCards = cardsMono.block();

            if (finCards == null || finCards.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Aucune carte Final Fantasy trouvée"));
            }

            logger.info("📦 {} cartes Final Fantasy récupérées", finCards.size());

            // Nettoyer les anciennes cartes
            logger.info("🗑️ Suppression anciennes cartes FIN...");
            cardRepository.deleteBySetCodeIgnoreCase("FIN");
            cardRepository.flush();

            // Sauvegarder avec TOUTES les colonnes renseignées
            int savedCount = 0;
            int errorCount = 0;
            ObjectMapper mapper = new ObjectMapper();

            for (MtgCard mtgCard : finCards) {
                try {
                    MagicCard entity = new MagicCard();
                    entity.setId(UlidUtils.generateUlidAsUuid());

                    // Champs de base
                    String externalId = mtgCard.id() != null ?
                            (mtgCard.id().length() > 20 ? mtgCard.id().substring(0, 20) : mtgCard.id())
                            : "fin_" + savedCount;
                    entity.setIdPrim(externalId);
                    entity.setZPostExtension("FIN");

                    // Numéro de carte
                    if (mtgCard.number() != null) {
                        try {
                            entity.setNumero(Integer.parseInt(mtgCard.number().replaceAll("\\D", "")));
                            entity.setNumber(mtgCard.number());
                        } catch (NumberFormatException e) {
                            entity.setNumber(mtgCard.number());
                        }
                    }

                    // Colonnes physiques
                    entity.setRarity(mtgCard.rarity() != null ? mtgCard.rarity() : "common");
                    entity.setLayout(mtgCard.layout() != null ? mtgCard.layout() : "normal");

                    if (mtgCard.colors() != null && !mtgCard.colors().isEmpty()) {
                        entity.setColors(String.join(",", mtgCard.colors()));
                    }

                    if (mtgCard.colorIdentity() != null && !mtgCard.colorIdentity().isEmpty()) {
                        entity.setColorIdentity(String.join(",", mtgCard.colorIdentity()));
                    }

                    if (mtgCard.types() != null && !mtgCard.types().isEmpty()) {
                        entity.setTypes(String.join(",", mtgCard.types()));
                    }

                    // Propriétés booléennes
                    entity.setHasFoil(true);
                    entity.setHasNonFoil(true);
                    entity.setIsFoilOnly(false);
                    entity.setIsOnlineOnly(false);
                    entity.setIsOversized(false);
                    entity.setIsTimeshifted(false);
                    entity.setIsToken(false);
                    entity.setIsReclassee(false);
                    entity.setHasDateFr(false);
                    entity.setIsAffichable(true);
                    entity.setHasRecherche(true);
                    entity.setCertifiable(false);
                    entity.setHasImg(false);

                    // JSON attributes (pour compatibilité)
                    Map<String, Object> attributes = new HashMap<>();
                    attributes.put("name", mtgCard.name());
                    attributes.put("manaCost", mtgCard.manaCost() != null ? mtgCard.manaCost() : "");
                    attributes.put("cmc", mtgCard.cmc() != null ? mtgCard.cmc() : 0);
                    attributes.put("type", mtgCard.type() != null ? mtgCard.type() : "");
                    attributes.put("text", mtgCard.text() != null ? mtgCard.text() : "");
                    attributes.put("artist", mtgCard.artist() != null ? mtgCard.artist() : "");
                    attributes.put("power", mtgCard.power() != null ? mtgCard.power() : "");
                    attributes.put("toughness", mtgCard.toughness() != null ? mtgCard.toughness() : "");
                    attributes.put("multiverseid", mtgCard.multiverseid() != null ? mtgCard.multiverseid() : 0);
                    attributes.put("setName", mtgCard.setName() != null ? mtgCard.setName() : "Final Fantasy");
                    attributes.put("imageUrl", mtgCard.imageUrl() != null ? mtgCard.imageUrl() : "");

                    entity.setAttributes(mapper.writeValueAsString(attributes));

                    // JSON allowed_notes (pour compatibilité)
                    Map<String, Object> allowedNotes = new HashMap<>();
                    allowedNotes.put("colors", mtgCard.colors() != null ? mtgCard.colors() : new ArrayList<>());
                    allowedNotes.put("colorIdentity", mtgCard.colorIdentity() != null ? mtgCard.colorIdentity() : new ArrayList<>());
                    allowedNotes.put("types", mtgCard.types() != null ? mtgCard.types() : new ArrayList<>());
                    allowedNotes.put("supertypes", mtgCard.supertypes() != null ? mtgCard.supertypes() : new ArrayList<>());
                    allowedNotes.put("subtypes", mtgCard.subtypes() != null ? mtgCard.subtypes() : new ArrayList<>());

                    entity.setAllowedNotes(mapper.writeValueAsString(allowedNotes));

                    // Traduction
                    MagicCardTranslation translation = new MagicCardTranslation();
                    translation.setId(UlidUtils.generateUlidAsUuid());
                    translation.setLocalization(Localization.USA);
                    translation.setAvailable(true);

                    String cardName = mtgCard.name() != null ? mtgCard.name() : ("Carte " + savedCount);
                    translation.setName(cardName);
                    translation.setLabelName(cardName);

                    entity.setTranslation(Localization.USA, translation);

                    // Sauvegarder l'entité complète
                    cardRepository.save(entity);
                    savedCount++;

                    if (savedCount % 50 == 0) {
                        logger.info("📊 {} cartes sauvegardées avec colonnes physiques...", savedCount);
                    }

                } catch (Exception e) {
                    logger.error("❌ Erreur sauvegarde carte {} : {}", mtgCard.name(), e.getMessage());
                    errorCount++;
                }
            }

            // Vérification finale
            long totalSaved = cardRepository.countBySetCode("FIN");

            Map<String, Object> result = new HashMap<>();
            result.put("cartesRecuperees", finCards.size());
            result.put("cartesSauvegardees", savedCount);
            result.put("cartesEnBase", totalSaved);
            result.put("erreurs", errorCount);
            result.put("succes", savedCount >= 300);
            result.put("methodeSauvegarde", "colonnes_physiques_propre");

            String message = String.format(
                    "Final Fantasy (solution propre): %d/%d cartes sauvegardées (%d en base, %d erreurs)",
                    savedCount, finCards.size(), totalSaved, errorCount
            );

            logger.info("✅ {}", message);
            return ResponseEntity.ok(ApiResponse.success(result, message));

        } catch (Exception e) {
            logger.error("❌ Erreur sauvegarde propre : {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Sauvegarde manuelle des cartes pour une extension donnée
     */
    @PostMapping("/save-cards-manually/{setCode}")
    public Mono<ResponseEntity<ApiResponse<String>>> saveCardsManually(@PathVariable String setCode) {
        logger.info("🎯 Demande de sauvegarde manuelle des cartes pour : {}", setCode);

        return mtgService.getCardsFromSet(setCode)
                .flatMap(cards -> {
                    if (cards.isEmpty()) {
                        return Mono.just(ResponseEntity.badRequest()
                                .body(ApiResponse.error("Aucune carte à sauvegarder pour " + setCode)));
                    }

                    return mtgService.saveCardsToDatabaseManually(setCode, cards)
                            .map(message -> ResponseEntity.ok(ApiResponse.success(message)))
                            .onErrorReturn(ResponseEntity.badRequest()
                                    .body(ApiResponse.error("Erreur lors de la sauvegarde des cartes pour " + setCode)));
                });
    }

    // ========== STATISTIQUES ET MONITORING ==========

    /**
     * Statistiques d'administration
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // Stats de base
            stats.put("totalCards", cardRepository.count());
            stats.put("totalSets", setRepository.count());
            stats.put("syncedSets", setRepository.countSyncedSets());
            stats.put("distinctArtists", cardRepository.countDistinctArtists());

            // Stats images
            long totalImages = cardRepository.count();
            long downloadedImages = cardRepository.findByImageDownloadedTrueAndLocalImagePathIsNotNull().size();

            Map<String, Object> imageStats = new HashMap<>();
            imageStats.put("total", totalImages);
            imageStats.put("downloaded", downloadedImages);
            imageStats.put("percentage", totalImages > 0 ?
                    Math.round((double) downloadedImages / totalImages * 100) : 0);

            stats.put("images", imageStats);

            // Stats par extension
            Map<String, Object> setStats = new HashMap<>();
            setStats.put("fin", cardRepository.countBySetCode("FIN"));
            setStats.put("totalCardsInSets", cardRepository.count());

            stats.put("sets", setStats);

            return ResponseEntity.ok(ApiResponse.success(stats, "Statistiques administrateur récupérées"));

        } catch (Exception e) {
            logger.error("❌ Erreur lors de la récupération des statistiques : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Erreur lors de la récupération des statistiques : " + e.getMessage()));
        }
    }

    // ========== DIAGNOSTIC ET VALIDATION ==========

    /**
     * Diagnostic des colonnes physiques
     */
    @GetMapping("/diagnostic-physical-columns")
    public ResponseEntity<ApiResponse<Map<String, Object>>> diagnosticPhysicalColumns() {
        try {
            logger.info("🔍 === DIAGNOSTIC COLONNES PHYSIQUES ===");

            Map<String, Object> result = new HashMap<>();

            long totalCards = cardRepository.countBySetCode("FIN");
            result.put("totalCartes", totalCards);

            if (totalCards == 0) {
                result.put("probleme", "Aucune carte FIN en base de données");
                return ResponseEntity.ok(ApiResponse.success(result, "Aucune carte FIN trouvée"));
            }

            // Analyser les colonnes physiques
            List<MagicCard> sampleCards = cardRepository.findBySetCode("FIN").stream()
                    .limit(5)
                    .collect(Collectors.toList());

            List<Map<String, Object>> carteAnalysees = new ArrayList<>();

            for (MagicCard card : sampleCards) {
                Map<String, Object> cardInfo = new HashMap<>();
                cardInfo.put("id", card.getId());
                cardInfo.put("numero", card.getNumero());
                cardInfo.put("name", card.getTranslation(Localization.USA) != null ?
                        card.getTranslation(Localization.USA).getName() : "N/A");

                // Vérifier les colonnes physiques
                cardInfo.put("rarityPhysique", card.getRarity());
                cardInfo.put("layoutPhysique", card.getLayout());
                cardInfo.put("colorsPhysique", card.getColors());
                cardInfo.put("colorIdentityPhysique", card.getColorIdentity());
                cardInfo.put("typesPhysique", card.getTypes());

                carteAnalysees.add(cardInfo);
            }

            result.put("echantillonCartes", carteAnalysees);

            return ResponseEntity.ok(ApiResponse.success(result,
                    String.format("Diagnostic colonnes physiques de %d cartes FIN terminé", totalCards)));

        } catch (Exception e) {
            logger.error("❌ Erreur diagnostic colonnes physiques : {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Validation d'une extension
     */
    @GetMapping("/validate-set/{setCode}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateSetAdaptation(@PathVariable String setCode) {
        try {
            Map<String, Object> validation = new HashMap<>();

            Optional<MagicSet> setOpt = setRepository.findByCode(setCode);
            if (setOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            MagicSet set = setOpt.get();
            boolean isValid = adaptationService.validateMagicSet(set);
            Map<String, Object> consistencyResult = persistenceService.validateSetConsistency(setCode);
            boolean isConsistent = consistencyResult.containsKey("success") &&
                    (Boolean) consistencyResult.getOrDefault("success", false);

            validation.put("setCode", setCode);
            validation.put("entityValid", isValid);
            validation.put("dataConsistent", isConsistent);
            validation.put("overallValid", isValid && isConsistent);

            // Détails de validation
            validation.put("hasValidType", set.getTypeMagic() != null);
            validation.put("hasValidTranslations", !set.getTranslations().isEmpty());
            validation.put("cardCount", cardRepository.countBySetCode(setCode));

            return ResponseEntity.ok(ApiResponse.success(validation, "Validation de l'adaptation pour " + setCode));

        } catch (Exception e) {
            logger.error("❌ Erreur validation set {} : {}", setCode, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur : " + e.getMessage()));
        }
    }

    // ========== NETTOYAGE ET MAINTENANCE ==========

    /**
     * Nettoyage post-adaptation
     */
    @PostMapping("/cleanup-adaptation")
    public ResponseEntity<ApiResponse<String>> cleanupAdaptation() {
        try {
            logger.info("🧹 Nettoyage post-adaptation");

            persistenceService.cleanupInconsistentData();

            return ResponseEntity.ok(ApiResponse.success("Nettoyage post-adaptation terminé"));

        } catch (Exception e) {
            logger.error("❌ Erreur nettoyage adaptation : {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur : " + e.getMessage()));
        }
    }

    /**
     * Forcer la synchronisation d'une extension
     */
    @PostMapping("/force-sync/{setCode}")
    public Mono<ResponseEntity<? extends ApiResponse<? extends Object>>> forceSyncSet(@PathVariable String setCode) {
        logger.info("🔄 Synchronisation forcée demandée pour : {}", setCode);

        return mtgService.forceSyncSet(setCode)
                .map(set -> {
                    try {
                        Map<String, Object> response = new HashMap<>();
                        response.put("setCode", set.code());
                        response.put("setName", set.name());
                        response.put("cardCount", set.cards() != null ? set.cards().size() : 0);
                        response.put("syncTimestamp", System.currentTimeMillis());

                        String message = String.format("Synchronisation forcée terminée pour %s (%d cartes)",
                                set.name(), set.cards() != null ? set.cards().size() : 0);

                        return ResponseEntity.ok(ApiResponse.success(response, message));

                    } catch (Exception e) {
                        String errorMessage = "Erreur lors de la synchronisation forcée de " + setCode + " : " + e.getMessage();
                        logger.error("❌ {}", errorMessage);
                        return ResponseEntity.badRequest()
                                .body(ApiResponse.error(errorMessage));
                    }
                })
                .onErrorReturn(ResponseEntity.badRequest()
                        .body(ApiResponse.error("Erreur lors de la synchronisation forcée")));
    }
}