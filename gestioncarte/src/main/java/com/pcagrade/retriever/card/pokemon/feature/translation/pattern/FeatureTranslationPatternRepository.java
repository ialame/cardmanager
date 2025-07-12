package com.pcagrade.retriever.card.pokemon.feature.translation.pattern;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.Localization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface FeatureTranslationPatternRepository extends JpaRepository<FeatureTranslationPattern, Integer> {

	List<FeatureTranslationPattern> findAllByFeatureIdAndSourceAndLocalization(Ulid featureId, String source, Localization localization);

	List<FeatureTranslationPattern> findAllByTitleIgnoreCaseAndSourceOrderById(String title, String source);
	List<FeatureTranslationPattern> findAllByImgHrefIgnoreCaseAndSourceOrderById(String imgHref, String source);

	List<FeatureTranslationPattern> findBySourceAndRegexIsNotNullOrderById(String source);

	List<FeatureTranslationPattern> findAllByFeatureId(Ulid featureId);
}
