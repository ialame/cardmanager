package com.pcagrade.retriever.card.pokemon.feature;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.commons.MapMultiHelper;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.feature.translation.pattern.FeatureTranslationPattern;
import com.pcagrade.retriever.card.pokemon.feature.translation.pattern.FeatureTranslationPatternDTO;
import com.pcagrade.retriever.card.pokemon.feature.translation.pattern.FeatureTranslationPatternRepository;
import com.pcagrade.retriever.card.pokemon.feature.translation.pattern.FeatureTranslationPatternService;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationDTO;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

@Service
@Transactional
@CacheConfig(cacheNames = { "feature" })
public class FeatureService {
	private static final String BULBAPEDIA = "bulbapedia";
	public static final String TO_JP = "-to-jp";

	@Autowired
	private FeatureMapper featureMapper;

	@Autowired
	private FeatureRepository featureRepository;

	@Autowired
	private FeatureTranslationPatternRepository featureTranslationPatternRepository;

	@Autowired
	private FeatureTranslationPatternService featureTranslationPatternService;

	@Cacheable
	public List<FeatureDTO> getFeatures() {
		return featureMapper.mapToDto(featureRepository.findAllByOrderByPcaName());
	}

	public Optional<FeatureDTO> findById(@Nullable Ulid id) {
		if (id == null) {
			return Optional.empty();
		}
		return featureRepository.findById(id)
				.map(featureMapper::mapToDto);
	}

	@Cacheable
	public FeatureDTO findBulbapediaFeature(String bulbapedia, String imgHrefBulbapedia) {
		return findFeature(BULBAPEDIA, bulbapedia, imgHrefBulbapedia);
	}

	@Cacheable
	public FeatureDTO findFeature(String source, String title, String href) {
		FeatureDTO value = null;

		if (StringUtils.isNotBlank(href)) {
			value = mapFeatureList(featureTranslationPatternRepository.findAllByImgHrefIgnoreCaseAndSourceOrderById(href, source), p -> StringUtils.equals(title, p.getTitle()));

		}
		if (value == null && StringUtils.isNotBlank(title)) {
			value = mapFeatureList(featureTranslationPatternRepository.findAllByTitleIgnoreCaseAndSourceOrderById(title, source),
					p -> StringUtils.containsIgnoreCase(href, p.getImgHref()),
					p -> StringUtils.equals(href, p.getImgHref()));
		}
		return value;
	}

	@SafeVarargs
	private FeatureDTO mapFeatureList(List<FeatureTranslationPattern> feature, Predicate<FeatureTranslationPattern>... filter) {
		if (feature.isEmpty()) {
			return null;
		} else if (feature.size() == 1) {
			return featureMapper.mapToDto(feature.get(0).getFeature());
		}
		return PCAUtils.progressiveFilter(feature, filter).stream()
				.map(p -> featureMapper.mapToDto(p.getFeature()))
				.findFirst()
				.orElse(null);
	}

	@Cacheable("inNameFeatures")
	public List<FeatureDTO> getInNameFeatures() {
		return featureTranslationPatternRepository.findBySourceAndRegexIsNotNullOrderById(BULBAPEDIA).stream()
				.filter(f -> StringUtils.isNotBlank(f.getRegex()))
				.sorted(FeatureTranslationPattern.LENGTH_COMPARATOR.reversed())
				.map(f -> featureMapper.mapToDto(f.getFeature()))
				.toList();
	}

	@Cacheable("PCAFeatures")
	public List<FeatureDTO> getPCAFeatures() {
		return featureRepository.findAll().stream()
				.filter(Feature::isPcaFeature)
				.map(featureMapper::mapToDto)
				.toList();
	}

	public void applyPCAFeatures(PokemonCardDTO card, PokemonCardTranslationDTO translation) {
		card.getFeatureIds().stream()
				.mapMulti(MapMultiHelper.mapOptional(this::findById))
				.filter(FeatureDTO::isPcaFeature)
				.forEach(f -> applyPCAFeatureToTranslation(f, translation));
	}

	public void applyPCAFeatureToTranslation(FeatureDTO feature, PokemonCardTranslationDTO translation) {
		var featureTranslation = feature.getTranslations().get(translation.getLocalization());

		translation.setName(StringUtils.appendIfMissingIgnoreCase(translation.getName(), " " + featureTranslation.getName()));
		translation.setLabelName(StringUtils.appendIfMissingIgnoreCase(translation.getLabelName(), " " + featureTranslation.getZebraName()));
	}

	public void applyFeaturePatterns(PokemonCardDTO card, PokemonCardTranslationDTO translation, String source) {
		var name = new AtomicReference<>(translation.getName());
		var labelName = new AtomicReference<>(translation.getLabelName());
		var localization = translation.getLocalization();

		card.getFeatureIds().stream()
				.mapMulti(MapMultiHelper.mapOptional(this::findById))
				.forEach(f -> {
					var toJp = source.endsWith(TO_JP);

					for (var pattern : featureTranslationPatternService.getPatterns(f.getId(), StringUtils.removeEnd(source, TO_JP), toJp ? Localization.USA : localization)) {
						name.set(applySinglePattern(localization, name.get(), f, pattern, false));
						labelName.set(applySinglePattern(localization, labelName.get(), f, pattern, true));
					}
				});
		translation.setName(PCAUtils.clean(name.get()));
		translation.setLabelName(PCAUtils.clean(labelName.get()));
	}

	private String applySinglePattern(Localization to, String name, FeatureDTO f, FeatureTranslationPatternDTO pattern, boolean label) {
		return pattern.matchesOrElse(name, (p, m) -> {
			var replacement = StringUtils.trimToEmpty(label ? pattern.getReplacementLabelName() : pattern.getReplacementName());

			if (StringUtils.isBlank(replacement)) {
				var featureTranslation = f.getTranslations().get(to);

				if (featureTranslation != null) {
					replacement = StringUtils.trimToEmpty(label ? featureTranslation.getZebraName() : featureTranslation.getName());
				} else {
					replacement = "";
				}
			}

			return PCAUtils.replaceAllWithSpace(p, name, replacement);
		}, () -> name);
	}

	@CacheEvict(cacheNames = { "feature", "inNameFeatures", "PCAFeatures" }, allEntries = true)
	@RevisionMessage("Sauvegarde de la particualrité {return}")
	public Ulid saveFeature(FeatureDTO featureDTO) {
		var feature = featureRepository.findByNullableId(featureDTO.getId()).orElseGet(Feature::new);
		var translations = featureDTO.getTranslations();

		translations.values().removeIf(translation -> StringUtils.isBlank(translation.getName()));
		featureMapper.update(feature, featureDTO);
		feature = featureRepository.save(feature);
		return feature.getId();
	}
}
