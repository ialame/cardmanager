package com.pcagrade.retriever.card.pokemon.feature.translation.pattern;

import com.pcagrade.retriever.annotation.RetrieverTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(FeatureTranslationPatternTestConfig.class)
class FeatureTranslationPatternMapperShould {

    @Autowired
    private FeatureTranslationPatternMapper featureTranslationPatternMapper;

    @Test
    void mapToDTO() {
        var pattern = FeatureTranslationPatternTestProvider.exPattern();
        var dto = featureTranslationPatternMapper.mapToDTO(pattern);

        assertThat(dto).usingRecursiveComparison()
                .isEqualTo(pattern);
    }

    @Test
    void mapFromDTO() {
        var dto = FeatureTranslationPatternTestProvider.megaPatternDTO();
        var pattern = featureTranslationPatternMapper.mapFromDTO(dto);

        assertThat(pattern).usingRecursiveComparison()
                .ignoringFields("feature", "featureTranslation")
                .isEqualTo(dto);
    }
}
