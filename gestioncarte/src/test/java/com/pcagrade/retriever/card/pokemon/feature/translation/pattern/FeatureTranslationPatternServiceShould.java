package com.pcagrade.retriever.card.pokemon.feature.translation.pattern;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.feature.FeatureTestProvider;
import com.pcagrade.mason.ulid.UlidHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(FeatureTranslationPatternTestConfig.class)
class FeatureTranslationPatternServiceShould {

    @Autowired
    private FeatureTranslationPatternRepository featureTranslationPatternRepository;

    @Autowired
    private FeatureTranslationPatternService featureTranslationPatternService;

    @ParameterizedTest
    @MethodSource("provideFeatureIds")
    void getByFeatureId(Ulid id) {
        var list = featureTranslationPatternService.getByFeatureId(id);

        assertThat(list).isNotEmpty();
    }

    @Test
    void setPatternsForFeature() {
        ArgumentCaptor<FeatureTranslationPattern> argument = ArgumentCaptor.forClass(FeatureTranslationPattern.class);

        Mockito.clearInvocations(featureTranslationPatternRepository);
        featureTranslationPatternService.setPatterns(FeatureTestProvider.MEGA_ID, List.of(FeatureTranslationPatternTestProvider.megaPatternDTO()));

        Mockito.verify(featureTranslationPatternRepository).save(argument.capture());
        assertThat(argument.getValue())
                .isNotNull()
                .matches(p -> p.getFeature() != null && UlidHelper.equals(FeatureTestProvider.MEGA_ID, p.getFeature().getId()))
                .matches(p -> p.getFeatureTranslation() != null);
    }

    private static Stream<Arguments> provideFeatureIds() {
        return Stream.of(
                Arguments.of(FeatureTestProvider.MEGA_ID),
                Arguments.of(FeatureTestProvider.EX_ID)
        );
    }
}
