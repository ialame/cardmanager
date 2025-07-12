package com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.handler;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.feature.FeatureDTO;
import com.pcagrade.retriever.card.pokemon.feature.FeatureService;
import com.pcagrade.retriever.card.pokemon.feature.translation.pattern.FeatureTranslationPatternService;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.BulbapediaPokemonCard;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.parser.IBulbapediaParser;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.ulid.UlidHelper;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@Order(1)
public class BulbapediaMappingFeatureHandler implements IBulbapediaMappingHandler {

    private static final Ulid DELTA_ID = Ulid.from("01G4GFKS1X47GNF95CT50H17JZ");
    private static final Ulid DELTA_BEFORE_AFTER_ID = Ulid.from("01G4GFKS1X47GNF95CT50H17KD");
    private static final Ulid DELTA_SPECIES_ID = Ulid.from("01G4GFKS1X47GNF95CT50H17KH");

    private static final List<Ulid> DELTA_FEATURES_ID = List.of(DELTA_ID, DELTA_BEFORE_AFTER_ID, DELTA_SPECIES_ID);

    @Autowired
    private IBulbapediaParser bulbapediaParser;
    @Autowired
    private FeatureService featureService;
    @Autowired
    private FeatureTranslationPatternService featureTranslationPatternService;

    @Override
    public void handle(PokemonCardDTO dto, BulbapediaPokemonCard card, Localization localization) {
        var features = card.getFeatures().stream()
                .map(b -> featureService.findBulbapediaFeature(b.getTitle(), b.getImgHref()))
                .toList();

        dto.setFeatureIds(features.stream()
                .filter(Objects::nonNull)
                .map(FeatureDTO::getId)
                .collect(Collectors.toList())); // NOSONAR (java:S6205) we need to add in name features

        for (var feature : featureService.getInNameFeatures()) {
            applyFeaturesPattern(dto, localization, feature);
        }

        var translation = dto.getTranslations().get(localization);

        translation.setName(formatName(translation.getName(), localization, features, false));
        translation.setLabelName(formatName(translation.getLabelName(), localization, features, true));

        var link = card.getLink();

        if (StringUtils.isNotBlank(link)) {
            handleDeltaSpecies(dto, link);
        }
    }

    private void applyFeaturesPattern(PokemonCardDTO dto, Localization localization, FeatureDTO feature) {
        if (feature == null) {
            return;
        }

        var id = feature.getId();

        if (id == null) {
            return;
        }

        var featureIds = dto.getFeatureIds();

        for (var featureTranslationPattern : featureTranslationPatternService.getPatterns(feature.getId(), "bulbapedia", Localization.USA)) {
            var translation = dto.getTranslations().get(localization);

            featureTranslationPattern.matches(translation.getName(), (p, m) -> {
                if (featureIds.stream().noneMatch(i -> UlidHelper.equals(i, id))) {
                    featureIds.add(id);
                }
                translation.setName(PCAUtils.clean(PCAUtils.replaceAllWithSpace(p, translation.getName(), PCAUtils.or(featureTranslationPattern.getReplacementName(), () -> feature.getTranslations().get(localization).getName()))));
                translation.setLabelName(PCAUtils.clean(PCAUtils.replaceAllWithSpace(p, translation.getLabelName(), PCAUtils.or(featureTranslationPattern.getReplacementLabelName(), () -> feature.getTranslations().get(localization).getZebraName()))));
            });
        }
    }

    @Nonnull
    private String formatName(String name, Localization localization, List<FeatureDTO> features, boolean label) {
        return MessageFormat.format(name.replace("'", "''"), features.stream()
                .filter(Objects::nonNull)
                .map(f -> {
                    var translation = f.getTranslations().get(localization);

                    return label ? translation.getZebraName() : translation.getName();
                })
                .toArray());
    }

    private void handleDeltaSpecies(PokemonCardDTO dto, String link) {
        var featureIds = new ArrayList<>(dto.getFeatureIds());
        Predicate<Ulid> isDelta = DELTA_FEATURES_ID::contains;

        if (featureIds.stream().anyMatch(isDelta) && bulbapediaParser.isDeltaSpecies(link)) {
            featureIds.removeIf(isDelta);
            featureIds.add(DELTA_SPECIES_ID);
        }
        dto.setFeatureIds(featureIds);
    }
}
