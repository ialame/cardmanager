package com.pcagrade.retriever.card.pokemon.feature.translation;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.AbstractTranslationMapper;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(config = PCAMapperConfig.class)
public abstract class FeatureTranslationMapper extends AbstractTranslationMapper<FeatureTranslation, FeatureTranslationDTO> {

	protected FeatureTranslationMapper() {
		super(FeatureTranslation.class);
	}

	public abstract FeatureTranslationDTO mapTranslationToDTO(FeatureTranslation feature);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "translatable", ignore = true)
	@Mapping(target = "zebraName", defaultValue = "")
	public abstract FeatureTranslation mapDTOToTranslation(FeatureTranslationDTO dto);

	@InheritConfiguration
	public abstract void update(@MappingTarget FeatureTranslation translation, FeatureTranslationDTO dto);

	public Map<Localization, FeatureTranslationDTO> translationListToDtoMap(List<FeatureTranslation> translations) {
		return translationListToDtoMap(translations, this::mapTranslationToDTO);
	}

	 public List<FeatureTranslation> dtoMapToTranslationList(Map<Localization, FeatureTranslationDTO> map) {
		return new ArrayList<>(dtoMapToTranslationList(map, this::mapDTOToTranslation));
	}

	public List<FeatureTranslation> update(@MappingTarget List<FeatureTranslation> translations, Map<Localization, FeatureTranslationDTO> map) {
		return this.update(translations, map, this::update, FeatureTranslation::new);
	}
	
}
