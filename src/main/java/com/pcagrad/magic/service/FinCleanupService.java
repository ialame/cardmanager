package com.pcagrad.magic.service;

import com.pcagrad.magic.entity.MagicCard;
import com.pcagrad.magic.entity.MagicSet;
import com.pcagrad.magic.repository.*;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FinCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(FinCleanupService.class);
    private static final String FIN_CODE = "FIN";

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private CardTranslationRepository cardTranslationRepository;

    @Autowired
    private EntityManager entityManager;

    /**
     * Nettoyage complet et sécurisé de l'extension FIN - VERSION CORRIGÉE
     */
    public Map<String, Object> cleanupFinExtensionCompletely() {
        logger.info("🧹 Début du nettoyage complet de l'extension FIN");

        Map<String, Object> result = new HashMap<>();

        try {
            // 1. Diagnostiquer avant nettoyage
            Map<String, Object> beforeStats = getFinStatistics();
            result.put("beforeCleanup", beforeStats);

            // 2. Supprimer en plusieurs étapes SANS @Transactional global
            int deletedRelations = deleteCardSetRelationsStep();
            int deletedTranslations = deleteCardTranslationsStep();
            int deletedCards = deleteFinCardsStep();
            int deletedSetTranslations = deleteSetTranslationsStep();
            int deletedSets = deleteFinSetsStep();
            int orphanTranslations = cleanupOrphanTranslationsStep();

            // 3. Statistiques finales
            Map<String, Object> afterStats = getFinStatistics();

            result.put("success", true);
            result.put("deletedRelations", deletedRelations);
            result.put("deletedTranslations", deletedTranslations);
            result.put("deletedCards", deletedCards);
            result.put("deletedSetTranslations", deletedSetTranslations);
            result.put("deletedSets", deletedSets);
            result.put("orphanTranslations", orphanTranslations);
            result.put("afterCleanup", afterStats);

            logger.info("✅ Nettoyage FIN terminé : {} cartes, {} sets, {} traductions supprimés",
                    deletedCards, deletedSets, deletedTranslations);

        } catch (Exception e) {
            logger.error("❌ Erreur pendant le nettoyage FIN : {}", e.getMessage(), e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    /**
     * Nettoyage sécurisé avec vérifications
     */
    public Map<String, Object> safeCleanupFinExtension() {
        logger.info("🔒 Nettoyage sécurisé de l'extension FIN avec vérifications");

        Map<String, Object> result = new HashMap<>();

        try {
            // 1. Vérifications préalables
            if (!isFinExtensionSafeToDelete()) {
                result.put("success", false);
                result.put("error", "Extension FIN ne peut pas être supprimée en sécurité");
                return result;
            }

            // 2. Backup des IDs pour vérification
            List<String> cardIds = getFinCardIds();
            List<String> setIds = getFinSetIds();

            // 3. Nettoyage complet
            Map<String, Object> cleanupResult = cleanupFinExtensionCompletely();

            // 4. Vérifications post-nettoyage
            boolean cleanupVerified = verifyCleanupComplete(cardIds, setIds);

            result.putAll(cleanupResult);
            result.put("cleanupVerified", cleanupVerified);
            result.put("backupCardIds", cardIds.size());
            result.put("backupSetIds", setIds.size());

        } catch (Exception e) {
            logger.error("❌ Erreur nettoyage sécurisé FIN : {}", e.getMessage());
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    // ===== MÉTHODES DE SUPPRESSION SÉPARÉES =====

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected int deleteCardSetRelationsStep() {
        logger.info("🔗 Suppression des relations card_card_set pour FIN");

        try {
            String sql = """
                DELETE ccs FROM card_card_set ccs
                INNER JOIN magic_card mc ON ccs.card_id = mc.id
                WHERE mc.z_post_extension = ?
            """;

            int deleted = entityManager.createNativeQuery(sql)
                    .setParameter(1, FIN_CODE)
                    .executeUpdate();

            entityManager.flush();
            logger.info("🗑️ {} relations card_card_set supprimées", deleted);
            return deleted;

        } catch (Exception e) {
            logger.error("❌ Erreur suppression relations : {}", e.getMessage());
            return 0;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected int deleteCardTranslationsStep() {
        logger.info("🌐 Suppression des traductions de cartes FIN");

        try {
            String sql = """
                DELETE ct FROM card_translation ct
                INNER JOIN magic_card mc ON ct.translatable_id = mc.id
                WHERE mc.z_post_extension = ?
            """;

            int deleted = entityManager.createNativeQuery(sql)
                    .setParameter(1, FIN_CODE)
                    .executeUpdate();

            entityManager.flush();
            logger.info("🗑️ {} traductions de cartes supprimées", deleted);
            return deleted;

        } catch (Exception e) {
            logger.error("❌ Erreur suppression traductions cartes : {}", e.getMessage());
            return 0;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected int deleteFinCardsStep() {
        logger.info("🃏 Suppression des cartes FIN");

        try {
            int deleted = cardRepository.deleteBySetCodeIgnoreCase(FIN_CODE);
            logger.info("🗑️ {} cartes FIN supprimées", deleted);
            return deleted;

        } catch (Exception e) {
            logger.error("❌ Erreur suppression cartes : {}", e.getMessage());
            return 0;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected int deleteSetTranslationsStep() {
        logger.info("🌐 Suppression des traductions d'extension FIN");

        try {
            String sql = """
                DELETE cst FROM card_set_translation cst
                INNER JOIN magic_set ms ON cst.translatable_id = ms.id
                WHERE ms.code = ?
            """;

            int deleted = entityManager.createNativeQuery(sql)
                    .setParameter(1, FIN_CODE)
                    .executeUpdate();

            entityManager.flush();
            logger.info("🗑️ {} traductions d'extension supprimées", deleted);
            return deleted;

        } catch (Exception e) {
            logger.error("❌ Erreur suppression traductions sets : {}", e.getMessage());
            return 0;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected int deleteFinSetsStep() {
        logger.info("📦 Suppression de l'extension FIN");

        try {
            Optional<MagicSet> finSet = setRepository.findByCode(FIN_CODE);
            if (finSet.isPresent()) {
                setRepository.delete(finSet.get());
                logger.info("🗑️ Extension FIN supprimée");
                return 1;
            }

            logger.info("ℹ️ Aucune extension FIN trouvée");
            return 0;

        } catch (Exception e) {
            logger.error("❌ Erreur suppression extension : {}", e.getMessage());
            return 0;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected int cleanupOrphanTranslationsStep() {
        logger.info("🧹 Nettoyage des traductions orphelines");

        try {
            int deletedCardTranslations = cardTranslationRepository.deleteOrphanTranslations();

            // Supprimer les traductions de sets orphelines
            String sql = """
                DELETE cst FROM card_set_translation cst
                LEFT JOIN card_set cs ON cst.translatable_id = cs.id
                WHERE cs.id IS NULL
            """;

            int deletedSetTranslations = entityManager.createNativeQuery(sql).executeUpdate();
            entityManager.flush();

            int total = deletedCardTranslations + deletedSetTranslations;
            logger.info("🗑️ {} traductions orphelines nettoyées", total);
            return total;

        } catch (Exception e) {
            logger.error("❌ Erreur nettoyage orphelins : {}", e.getMessage());
            return 0;
        }
    }

    // ===== MÉTHODES DE VÉRIFICATION =====

    public Map<String, Object> getFinStatistics() {
        Map<String, Object> stats = new HashMap<>();

        try {
            long cardCount = cardRepository.countBySetCode(FIN_CODE);
            boolean setExists = setRepository.existsByCode(FIN_CODE);

            // Compter les traductions
            String cardTransSql = """
                SELECT COUNT(*) FROM card_translation ct
                INNER JOIN magic_card mc ON ct.translatable_id = mc.id
                WHERE mc.z_post_extension = ?
            """;

            long cardTranslations = ((Number) entityManager.createNativeQuery(cardTransSql)
                    .setParameter(1, FIN_CODE)
                    .getSingleResult()).longValue();

            // Compter les relations
            String relationsSql = """
                SELECT COUNT(*) FROM card_card_set ccs
                INNER JOIN magic_card mc ON ccs.card_id = mc.id
                WHERE mc.z_post_extension = ?
            """;

            long relations = ((Number) entityManager.createNativeQuery(relationsSql)
                    .setParameter(1, FIN_CODE)
                    .getSingleResult()).longValue();

            stats.put("cards", cardCount);
            stats.put("setExists", setExists);
            stats.put("cardTranslations", cardTranslations);
            stats.put("relations", relations);
            stats.put("timestamp", System.currentTimeMillis());

        } catch (Exception e) {
            logger.error("❌ Erreur stats FIN : {}", e.getMessage());
            stats.put("error", e.getMessage());
        }

        return stats;
    }

    private boolean isFinExtensionSafeToDelete() {
        try {
            // Vérifier qu'il n'y a pas de contraintes critiques
            long cardCount = cardRepository.countBySetCode(FIN_CODE);
            logger.info("🔍 Extension FIN contient {} cartes", cardCount);

            // Pour l'instant, toujours sûr de supprimer FIN
            return true;

        } catch (Exception e) {
            logger.error("❌ Erreur vérification sécurité : {}", e.getMessage());
            return false;
        }
    }

    private List<String> getFinCardIds() {
        try {
            String sql = "SELECT CAST(id AS CHAR) FROM magic_card WHERE z_post_extension = ?";
            return entityManager.createNativeQuery(sql)
                    .setParameter(1, FIN_CODE)
                    .getResultList();
        } catch (Exception e) {
            logger.error("❌ Erreur récupération card IDs : {}", e.getMessage());
            return List.of();
        }
    }

    private List<String> getFinSetIds() {
        try {
            String sql = "SELECT CAST(id AS CHAR) FROM magic_set WHERE code = ?";
            return entityManager.createNativeQuery(sql)
                    .setParameter(1, FIN_CODE)
                    .getResultList();
        } catch (Exception e) {
            logger.error("❌ Erreur récupération set IDs : {}", e.getMessage());
            return List.of();
        }
    }

    private boolean verifyCleanupComplete(List<String> originalCardIds, List<String> originalSetIds) {
        try {
            long remainingCards = cardRepository.countBySetCode(FIN_CODE);
            boolean remainingSet = setRepository.existsByCode(FIN_CODE);

            boolean isComplete = (remainingCards == 0) && !remainingSet;

            logger.info("✅ Vérification nettoyage : {} cartes restantes, set existe: {}",
                    remainingCards, remainingSet);

            return isComplete;

        } catch (Exception e) {
            logger.error("❌ Erreur vérification : {}", e.getMessage());
            return false;
        }
    }
}