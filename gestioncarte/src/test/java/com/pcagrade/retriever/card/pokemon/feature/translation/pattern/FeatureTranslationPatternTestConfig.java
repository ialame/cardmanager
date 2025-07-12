package com.pcagrade.retriever.card.pokemon.feature.translation.pattern;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.feature.FeatureRepositoryTestConfig;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.ulid.UlidHelper;
import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;

@RetrieverTestConfiguration
@Import(FeatureRepositoryTestConfig.class)
public class FeatureTranslationPatternTestConfig {

    @Bean
    public FeatureTranslationPatternRepository featureTranslationPatternRepository() {
        var list = List.of(
                FeatureTranslationPatternTestProvider.megaPattern(),
                FeatureTranslationPatternTestProvider.exPattern(),
                FeatureTranslationPatternTestProvider.exPatternWikiFr(),
                FeatureTranslationPatternTestProvider.exNonTeraPatternWikiFr()
        );

        var repository = RetrieverTestUtils.mockRepository(FeatureTranslationPatternRepository.class, list, FeatureTranslationPattern::getId);

        Mockito.when(repository.findAllByFeatureIdAndSourceAndLocalization(Mockito.any(Ulid.class), Mockito.anyString(), Mockito.any(Localization.class))).then(i -> {
            var featureId = i.getArgument(0, Ulid.class);
            var source = i.getArgument(1, String.class);
            var localization = i.getArgument(2, Localization.class);

            return list.stream()
                    .filter(p -> UlidHelper.equals(p.getFeature().getId(), featureId) && StringUtils.equals(p.getSource(), source) && p.getLocalization().equals(localization))
                    .toList();
        });
        Mockito.when(repository.findBySourceAndRegexIsNotNullOrderById(Mockito.anyString())).then(i -> {
            var source = i.getArgument(0, String.class);

            return list.stream()
                    .filter(p -> StringUtils.equals(p.getSource(), source) && StringUtils.isNotBlank(p.getRegex()))
                    .toList();
        });
        Mockito.when(repository.findAllByFeatureId(Mockito.any(Ulid.class))).then(i -> {
            var featureId = i.getArgument(0, Ulid.class);

            return list.stream()
                    .filter(p -> UlidHelper.equals(p.getFeature().getId(), featureId))
                    .toList();
        });
        Mockito.when(repository.findAllByImgHrefIgnoreCaseAndSourceOrderById(Mockito.anyString(), Mockito.anyString())).then(i -> {
            String imgHref = i.getArgument(0, String.class);
            String source = i.getArgument(1, String.class);

            return list.stream()
                    .filter(p -> StringUtils.equalsIgnoreCase(p.getImgHref(), imgHref) && StringUtils.equals(p.getSource(), source))
                    .toList();
        });
        Mockito.when(repository.findAllByTitleIgnoreCaseAndSourceOrderById(Mockito.anyString(), Mockito.anyString())).then(i -> {
            String title = i.getArgument(0, String.class);
            String source = i.getArgument(1, String.class);

            return list.stream()
                    .filter(p -> StringUtils.equalsIgnoreCase(p.getTitle(), title) && StringUtils.equals(p.getSource(), source))
                    .toList();
        });
        return repository;
    }

    @Bean
    public FeatureTranslationPatternMapper featureTranslationPatternMapper() {
        return new FeatureTranslationPatternMapperImpl();
    }

    @Bean
    public FeatureTranslationPatternService featureTranslationPatternService() {
        return new FeatureTranslationPatternService();
    }

}
