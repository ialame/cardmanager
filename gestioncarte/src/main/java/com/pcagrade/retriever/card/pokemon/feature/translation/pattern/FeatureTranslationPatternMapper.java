package com.pcagrade.retriever.card.pokemon.feature.translation.pattern;

import com.pcagrade.retriever.PCAMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = PCAMapperConfig.class)
public interface FeatureTranslationPatternMapper {

    FeatureTranslationPatternDTO mapToDTO(FeatureTranslationPattern featureTranslationPattern);

    FeatureTranslationPattern mapFromDTO(FeatureTranslationPatternDTO dto);

}
