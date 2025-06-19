package com.pcagrad.magic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrad.magic.entity.*;
import com.pcagrad.magic.model.MtgCard;
import com.pcagrad.magic.model.MtgSet; // AJOUT: Import manquant
import com.pcagrad.magic.repository.*;
import com.pcagrad.magic.util.Localization;
import com.pcagrad.magic.util.UlidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class CardPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(CardPersistenceService.class);

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private CardTranslationRepository cardTranslationRepository;

    @Autowired
    private ImageDownloadService imageDownloadService;



    // ===== AJOUTEZ cette annotation dans votre classe =====
    @PersistenceContext
    private EntityManager entityManager;

    // Note: EntityAdaptationService retiré car la méthode n'existe pas
    // La logique d'adaptation est maintenant directement dans ce service

    // ===============================
    // MÉTHODE PRINCIPALE CORRIGÉE
    // ===============================

    /**
     * Sauvegarde ou met à jour une carte - VERSION CORRIGÉE AVEC RELATION
     */
    @Transactional
    public MagicCard saveOrUpdateCardAdapted(MtgCard mtgCard, String setCode) {
        if (mtgCard.id() == null || mtgCard.id().isEmpty()) {
            logger.warn("⚠️ Carte sans ID externe ignorée : {}", mtgCard.name());
            return null;
        }

        // 1. RÉCUPÉRER LE CARDSET CORRESPONDANT
        Optional<MagicSet> setEntity = setRepository.findByCode(setCode);
        if (setEntity.isEmpty()) {
            logger.error("❌ Extension {} non trouvée en base", setCode);
            return null;
        }
        MagicSet cardSet = setEntity.get();

        // 2. CHERCHER UNE CARTE EXISTANTE
        Optional<MagicCard> existingCard = cardRepository.findByExternalIdAndSetCode(mtgCard.id(), setCode);
        MagicCard cardEntity;

        if (existingCard.isPresent()) {
            cardEntity = existingCard.get();
            updateCardEntityAdapted(cardEntity, mtgCard);
            logger.debug("🔄 Mise à jour carte existante : {}", mtgCard.name());
        } else {
            // Vérifier s'il y a une carte avec le même nom dans cette extension
            List<MagicCard> sameName = cardRepository.findByNameAndSetCode(mtgCard.name(), setCode);
            if (!sameName.isEmpty()) {
                cardEntity = sameName.get(0);
                cardEntity.setExternalId(mtgCard.id());
                updateCardEntityAdapted(cardEntity, mtgCard);
                logger.debug("🔄 Carte existante trouvée par nom : {}", mtgCard.name());
            } else {
                cardEntity = createCardEntityAdapted(mtgCard, setCode);
                logger.debug("✨ Nouvelle carte créée : {}", mtgCard.name());
            }
        }

        // 3. *** CORRECTION PRINCIPALE : ÉTABLIR LA RELATION CARD-CARDSET ***
        establishCardSetRelation(cardEntity, cardSet);

        try {
            // 4. SAUVEGARDER AVEC TRADUCTIONS
            return saveCardWithTranslations(cardEntity);
        } catch (Exception e) {
            logger.error("❌ Erreur sauvegarde carte {} : {}", mtgCard.name(), e.getMessage());
            return null;
        }
    }



    /**
     * MÉTHODE DE CRÉATION D'ENTITÉ CORRIGÉE - SANS DÉPENDANCE
     */
    private MagicCard createCardEntityAdapted(MtgCard mtgCard, String setCode) {
        MagicCard cardEntity = new MagicCard();

        // Générer un UUID unique pour la carte
        cardEntity.setId(UlidUtils.generateUlidAsUuid());

        // Adapter les données de base directement
        adaptCardDataToEntity(cardEntity, mtgCard, setCode);

        // Créer les traductions (CORRIGÉ pour votre model)
        createCardTranslationsSimple(cardEntity, mtgCard);

        return cardEntity;
    }

    /**
     * MÉTHODE DE MISE À JOUR D'ENTITÉ CORRIGÉE - SANS DÉPENDANCE
     */
    private void updateCardEntityAdapted(MagicCard cardEntity, MtgCard mtgCard) {
        // Mettre à jour les données de base directement
        adaptCardDataToEntity(cardEntity, mtgCard, cardEntity.getZPostExtension());

        // Mettre à jour les traductions existantes (CORRIGÉ)
        updateCardTranslationsSimple(cardEntity, mtgCard);
    }

    /**
     * NOUVELLE MÉTHODE : Adaptation directe des données de carte
     */
    private void adaptCardDataToEntity(MagicCard cardEntity, MtgCard mtgCard, String setCode) {
        // ID externe (Scryfall ID)
        String externalId = mtgCard.id();
        if (externalId != null && externalId.length() > 20) {
            // Tronquer si trop long pour la colonne id_prim
            externalId = externalId.substring(0, 20);
        }
        cardEntity.setIdPrim(externalId);

        // Code de l'extension
        cardEntity.setZPostExtension(setCode);

        // Numéro de carte
        if (mtgCard.number() != null) {
            cardEntity.setNumber(mtgCard.number());
            try {
                cardEntity.setNumero(Integer.parseInt(mtgCard.number().replaceAll("[^0-9]", "")));
            } catch (NumberFormatException e) {
                cardEntity.setNumero(0);
            }
        }

        // Attributs JSON - stocker les données MTG importantes
        try {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("manaCost", mtgCard.manaCost());
            attributes.put("cmc", mtgCard.cmc());
            attributes.put("type", mtgCard.type());
            attributes.put("rarity", mtgCard.rarity());
            attributes.put("text", mtgCard.text());
            attributes.put("power", mtgCard.power());
            attributes.put("toughness", mtgCard.toughness());
            attributes.put("artist", mtgCard.artist());
            attributes.put("layout", mtgCard.layout());
            attributes.put("multiverseid", mtgCard.multiverseid());

            // Convertir en JSON
            ObjectMapper mapper = new ObjectMapper();
            cardEntity.setAttributes(mapper.writeValueAsString(attributes));
        } catch (Exception e) {
            logger.warn("⚠️ Erreur création JSON attributes pour {} : {}", mtgCard.name(), e.getMessage());
            cardEntity.setAttributes("{}");
        }

        // Notes autorisées - stocker colors, colorIdentity, types, etc.
        try {
            Map<String, Object> allowedNotes = new HashMap<>();
            allowedNotes.put("colors", mtgCard.colors());
            allowedNotes.put("colorIdentity", mtgCard.colorIdentity());
            allowedNotes.put("supertypes", mtgCard.supertypes());
            allowedNotes.put("types", mtgCard.types());
            allowedNotes.put("subtypes", mtgCard.subtypes());

            ObjectMapper mapper = new ObjectMapper();
            cardEntity.setAllowedNotes(mapper.writeValueAsString(allowedNotes));
        } catch (Exception e) {
            logger.warn("⚠️ Erreur création JSON allowedNotes pour {} : {}", mtgCard.name(), e.getMessage());
            cardEntity.setAllowedNotes("[]");
        }

        // URL d'image originale (pour téléchargement ultérieur)
        if (mtgCard.imageUrl() != null) {
            // CORRECTION: Utiliser imageUrl() au lieu de imageUrl
            cardEntity.setOriginalImageUrl(mtgCard.imageUrl());
        }

        // Propriétés booléennes
        cardEntity.setHasImg(mtgCard.imageUrl() != null);
        cardEntity.setCertifiable(true);
        cardEntity.setIsAffichable(true);
        cardEntity.setHasRecherche(true);
        cardEntity.setHasDateFr(false);
    }

    /**
     * CRÉATION DES TRADUCTIONS SIMPLIFIÉE (sans foreignNames)
     */
    private void createCardTranslationsSimple(MagicCard cardEntity, MtgCard mtgCard) {
        // Traduction anglaise (USA) - SEULE DISPONIBLE dans votre model
        CardTranslation usaTranslation = new CardTranslation();
        usaTranslation.setId(UlidUtils.generateUlidAsUuid());
        usaTranslation.setName(mtgCard.name());
        usaTranslation.setLabelName(mtgCard.name());
        usaTranslation.setLocalization(Localization.USA);
        usaTranslation.setAvailable(true);
        usaTranslation.setTranslatable(cardEntity);

        cardEntity.setTranslation(Localization.USA, usaTranslation);

        // Note: Pas de traduction française car foreignNames() n'existe pas
        // dans votre model MtgCard actuel
    }

    /**
     * MISE À JOUR DES TRADUCTIONS SIMPLIFIÉE
     */
    private void updateCardTranslationsSimple(MagicCard cardEntity, MtgCard mtgCard) {
        // Mettre à jour la traduction anglaise
        CardTranslation usaTranslation = cardEntity.getTranslation(Localization.USA);
        if (usaTranslation != null) {
            usaTranslation.setName(mtgCard.name());
            usaTranslation.setLabelName(mtgCard.name());
        } else {
            createCardTranslationsSimple(cardEntity, mtgCard);
        }
    }

    /**
     * VÉRIFICATION D'EXISTENCE D'EXTENSION SIMPLIFIÉE
     */
    private void ensureSetExistsAdapted(String setCode, List<MtgCard> cards) {
        Optional<MagicSet> existingSet = setRepository.findByCode(setCode);
        if (existingSet.isEmpty()) {
            logger.error("❌ Extension {} non trouvée en base. Création manuelle requise.", setCode);
            throw new RuntimeException("Extension " + setCode + " non trouvée. Veuillez d'abord créer l'extension.");
        }
    }

    /**
     * MISE À JOUR DES STATISTIQUES D'EXTENSION
     */
    private void updateSetStatisticsAdapted(String setCode) {
        try {
            Optional<MagicSet> setEntity = setRepository.findByCode(setCode);
            if (setEntity.isPresent()) {
                MagicSet magicSet = setEntity.get();
                long cardCount = cardRepository.countBySetCode(setCode);
                magicSet.setNbCartes((int) cardCount);
                setRepository.save(magicSet);
                logger.debug("📊 Statistiques mises à jour pour {} : {} cartes", setCode, cardCount);
            }
        } catch (Exception e) {
            logger.warn("⚠️ Erreur mise à jour statistiques pour {} : {}", setCode, e.getMessage());
        }
    }

    // ===============================
    // MÉTHODES PUBLIQUES EXISTANTES
    // ===============================

    @Async
    public CompletableFuture<Integer> saveCardsForSet(String setCode, List<MtgCard> cards) {
        logger.info("💾 Début de la sauvegarde de {} cartes pour l'extension {}", cards.size(), setCode);

        return CompletableFuture.supplyAsync(() -> {
            ensureSetExistsAdapted(setCode, cards);

            int savedCount = 0;
            int skippedCount = 0;

            for (MtgCard mtgCard : cards) {
                try {
                    MagicCard result = saveOrUpdateCardAdapted(mtgCard, setCode);
                    if (result != null) {
                        savedCount++;

                        // Téléchargement d'image en arrière-plan
                        if (result.getOriginalImageUrl() != null && !result.getOriginalImageUrl().isEmpty()) {
                            try {
                                imageDownloadService.downloadCardImage(result);
                            } catch (Exception e) {
                                logger.warn("⚠️ Erreur téléchargement image pour {} : {}", mtgCard.name(), e.getMessage());
                            }
                        }
                    } else {
                        skippedCount++;
                    }
                } catch (Exception e) {
                    logger.error("❌ Erreur sauvegarde carte {} : {}", mtgCard.name(), e.getMessage());
                    skippedCount++;
                }
            }

            logger.info("✅ Sauvegarde terminée pour {} : {} sauvées, {} ignorées", setCode, savedCount, skippedCount);
            return savedCount;
        });
    }

    public int saveCards(List<MtgCard> cards, String setCode) {
        logger.info("💾 Début de la sauvegarde synchrone de {} cartes pour {}", cards.size(), setCode);

        ensureSetExistsAdapted(setCode, cards);

        int savedCount = 0;
        int skippedCount = 0;

        for (MtgCard mtgCard : cards) {
            try {
                MagicCard result = saveOrUpdateCardAdapted(mtgCard, setCode);
                if (result != null) {
                    savedCount++;
                } else {
                    skippedCount++;
                }
            } catch (Exception e) {
                logger.error("❌ Erreur sauvegarde carte {} : {}", mtgCard.name(), e.getMessage());
                skippedCount++;
            }
        }

        logger.info("✅ Sauvegarde terminée pour {} : {} sauvées, {} ignorées", setCode, savedCount, skippedCount);
        return savedCount;
    }

    // ===== MÉTHODES MANQUANTES POUR LA COMPATIBILITÉ =====

    /**
     * Méthode manquante : saveOrUpdateSet
     */
    public MagicSet saveOrUpdateSet(MtgSet mtgSet) {
        logger.debug("💾 Sauvegarde de l'extension : {} ({})", mtgSet.name(), mtgSet.code());

        Optional<MagicSet> existingSet = setRepository.findByCode(mtgSet.code());
        MagicSet setEntity;

        if (existingSet.isPresent()) {
            setEntity = existingSet.get();
            updateSetEntity(setEntity, mtgSet);
            logger.debug("🔄 Mise à jour extension existante : {}", mtgSet.name());
        } else {
            setEntity = createSetEntity(mtgSet);
            logger.debug("✨ Nouvelle extension créée : {}", mtgSet.name());
        }

        return setRepository.save(setEntity);
    }

    /**
     * Méthode manquante : validateSetConsistency
     */
    public Map<String, Object> validateSetConsistency(String setCode) {
        Map<String, Object> result = new HashMap<>();
        try {
            Optional<MagicSet> setEntity = setRepository.findByCode(setCode);
            if (setEntity.isEmpty()) {
                result.put("error", "Extension non trouvée : " + setCode);
                result.put("valid", false);
                return result;
            }

            MagicSet magicSet = setEntity.get();
            long cardCount = cardRepository.countBySetCode(setCode);

            result.put("setCode", setCode);
            result.put("expectedCards", magicSet.getNbCartes());
            result.put("actualCards", cardCount);
            result.put("valid", magicSet.getNbCartes() == null || magicSet.getNbCartes().equals((int)cardCount));
            result.put("message", String.format("Extension %s : %d cartes attendues, %d cartes trouvées",
                    setCode, magicSet.getNbCartes(), cardCount));

        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("valid", false);
        }
        return result;
    }

    /**
     * Méthode manquante : cleanupInconsistentData
     */
    public Map<String, Object> cleanupInconsistentData() {
        Map<String, Object> result = new HashMap<>();
        try {
            // Nettoyer les cartes sans traductions
            int deletedCards = cardRepository.deleteCardsWithoutTranslations();

            result.put("deletedCards", deletedCards);
            result.put("success", true);
            result.put("message", String.format("Nettoyage terminé : %d cartes supprimées", deletedCards));

        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("success", false);
        }
        return result;
    }

    // ===== MÉTHODES UTILITAIRES PRIVÉES POUR LES EXTENSIONS =====

    private MagicSet createSetEntity(MtgSet mtgSet) {
        MagicSet setEntity = new MagicSet();
        updateSetEntity(setEntity, mtgSet);
        return setEntity;
    }

    private void updateSetEntity(MagicSet setEntity, MtgSet mtgSet) {
        setEntity.setCode(mtgSet.code());

        // Créer une traduction pour le nom
        CardSetTranslation translation = new CardSetTranslation();
        translation.setName(mtgSet.name());
        translation.setLabelName(mtgSet.name());
        translation.setLocalization(Localization.USA);
        translation.setAvailable(true);
        translation.setTranslatable(setEntity);

        setEntity.setTranslation(Localization.USA, translation);

        // Date de sortie
        if (mtgSet.getParsedReleaseDate() != null) {
            setEntity.setReleaseDate(mtgSet.getParsedReleaseDate());
        }
    }

    // ===== CORRECTIONS À APPORTER DANS CardPersistenceService.java =====

    /**
     * *** MÉTHODE CORRIGÉE : Établir la relation bidirectionnelle Card-CardSet ***
     */
    @Transactional
    protected void establishCardSetRelation(MagicCard card, MagicSet magicSet) {
        try {
            // Vérifier si la relation existe déjà
            boolean relationExists = card.getCardSets().stream()
                    .anyMatch(cs -> cs.getId().equals(magicSet.getId()));

            if (!relationExists) {
                // Ajouter l'extension à la carte
                card.getCardSets().add(magicSet);

                // Ajouter la carte à l'extension (relation bidirectionnelle)
                if (!magicSet.getCards().contains(card)) {
                    magicSet.getCards().add(card);
                }

                logger.debug("🔗 Relation établie entre carte {} et extension {}",
                        card.getName(), magicSet.getCode());
            } else {
                logger.debug("🔗 Relation déjà existante pour {} - {}",
                        card.getName(), magicSet.getCode());
            }
        } catch (Exception e) {
            logger.error("❌ Erreur établissement relation pour {} : {}",
                    card.getName(), e.getMessage());
        }
    }

    /**
     * *** AMÉLIORATION : Méthode de sauvegarde avec gestion des relations ***
     */
    @Transactional
    protected MagicCard saveCardWithTranslations(MagicCard cardEntity) {
        try {
            // 1. Sauvegarder la carte d'abord pour obtenir un ID
            MagicCard savedCard = cardRepository.save(cardEntity);

            // 2. Sauvegarder les traductions séparément si nécessaire
            if (savedCard.getTranslations() != null && !savedCard.getTranslations().isEmpty()) {
                for (CardTranslation translation : savedCard.getTranslations()) {
                    if (translation.getId() == null) {
                        translation.setId(UlidUtils.generateUlidAsUuid());
                    }
                    translation.setTranslatable(savedCard);
                    cardTranslationRepository.save(translation);
                }
            }

            // 3. Flush pour s'assurer que tout est persisté (y compris les relations Many-to-Many)
            cardRepository.flush();

            logger.debug("💾 Carte et traductions sauvegardées : {}", savedCard.getName());
            return savedCard;

        } catch (Exception e) {
            logger.error("❌ Erreur lors de la sauvegarde complète : {}", e.getMessage());
            throw e;
        }
    }

    /**
     * *** MÉTHODE D'AUTO-RÉPARATION : Pour corriger les relations manquantes ***
     */
    @Transactional
    public int repairMissingRelationsForSet(String setCode) {
        logger.info("🔧 Réparation des relations manquantes pour {}", setCode);

        try {
            // Trouver l'extension
            Optional<MagicSet> setEntity = setRepository.findByCode(setCode);
            if (setEntity.isEmpty()) {
                logger.error("❌ Extension {} non trouvée", setCode);
                return 0;
            }
            MagicSet magicSet = setEntity.get();

            // Trouver toutes les cartes de cette extension sans relation
            List<MagicCard> cardsWithoutRelations = cardRepository.findCardsWithoutSetRelation(setCode);

            int repairedCount = 0;
            for (MagicCard card : cardsWithoutRelations) {
                establishCardSetRelation(card, magicSet);
                cardRepository.save(card);
                repairedCount++;
            }

            cardRepository.flush();
            logger.info("✅ {} relations réparées pour {}", repairedCount, setCode);
            return repairedCount;

        } catch (Exception e) {
            logger.error("❌ Erreur réparation relations {} : {}", setCode, e.getMessage());
            return 0;
        }
    }

    /**
     * *** ENDPOINT DE DEBUG : Pour diagnostiquer les relations ***
     */
    public Map<String, Object> diagnoseSetRelations(String setCode) {
        Map<String, Object> diagnosis = new HashMap<>();

        try {
            // Compter les cartes dans magic_card
            long cardsInTable = cardRepository.countBySetCode(setCode);

            // Compter les relations dans card_card_set
            long relationsCount = cardRepository.countRelationsBySetCode(setCode);

            // Identifier les cartes sans relations
            List<MagicCard> cardsWithoutRelations = cardRepository.findCardsWithoutSetRelation(setCode);

            diagnosis.put("setCode", setCode);
            diagnosis.put("cardsInTable", cardsInTable);
            diagnosis.put("relationsCount", relationsCount);
            diagnosis.put("cardsWithoutRelations", cardsWithoutRelations.size());
            diagnosis.put("allRelationsExist", cardsInTable == relationsCount);

            if (cardsInTable != relationsCount) {
                diagnosis.put("needsRepair", true);
                diagnosis.put("missingRelations", cardsInTable - relationsCount);
            } else {
                diagnosis.put("needsRepair", false);
            }

        } catch (Exception e) {
            diagnosis.put("error", e.getMessage());
        }

        return diagnosis;
    }

    // ===== SOLUTION MINIMALE: Ajout d'UNE SEULE méthode dans CardPersistenceService =====

/**
 * AJOUTEZ SEULEMENT cette méthode dans votre CardPersistenceService.java existant
 * Ne changez RIEN d'autre pour l'instant
 */

    /**
     * Méthode pour réparer les relations manquantes en masse
     */
    @Transactional
    public int repairAllMissingRelations() {
        logger.info("🔧 Réparation de TOUTES les relations manquantes");

        try {
            // Utiliser une requête native SQL directe pour éviter les problèmes JPA
            String sql = """
            INSERT INTO card_card_set (card_id, card_set_id)
            SELECT DISTINCT mc.id, ms.id
            FROM magic_card mc
            JOIN magic_set ms ON mc.z_post_extension = ms.code
            WHERE NOT EXISTS (
                SELECT 1 FROM card_card_set ccs 
                WHERE ccs.card_id = mc.id AND ccs.card_set_id = ms.id
            )
            """;

            int insertedRelations = entityManager.createNativeQuery(sql).executeUpdate();

            logger.info("✅ {} relations réparées automatiquement", insertedRelations);
            return insertedRelations;

        } catch (Exception e) {
            logger.error("❌ Erreur réparation massive : {}", e.getMessage());
            return 0;
        }
    }



}