package com.pcagrade.retriever.card.pokemon.feature;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.pokemon.feature.translation.FeatureTranslationMapper;
import com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction.BulbapediaExtractionFeature;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = PCAMapperConfig.class,
		collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
		uses = FeatureTranslationMapper.class)
public interface FeatureMapper {

	FeatureDTO mapToDto(Feature feature);

	Feature mapFromDto(FeatureDTO features);

	void update(@MappingTarget Feature feature, FeatureDTO dto);

	BulbapediaExtractionFeature mapToBulbapediaFeature(Feature feature);

	BulbapediaExtractionFeature mapToBulbapediaFeature(FeatureDTO feature);

	List<FeatureDTO> mapToDto(Iterable<Feature> features);

	List<Feature> mapFromDto(Iterable<FeatureDTO> features);

}
