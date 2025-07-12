package com.pcagrade.retriever.card.promo.event.trait.translation;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.AbstractTranslationMapper;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Map;

@Mapper(config = PCAMapperConfig.class)
public abstract class PromoCardEventTraitTranslationMapper extends AbstractTranslationMapper<PromoCardEventTraitTranslation, PromoCardEventTraitTranslationDTO> {

    protected PromoCardEventTraitTranslationMapper() {
        super(PromoCardEventTraitTranslation.class);
    }

    public abstract PromoCardEventTraitTranslationDTO mapTranslationToDTO(PromoCardEventTraitTranslation trait);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "translatable", ignore = true)
    public abstract PromoCardEventTraitTranslation mapDTOToTranslation(PromoCardEventTraitTranslationDTO dto);

    @InheritConfiguration
    public abstract void update(@MappingTarget PromoCardEventTraitTranslation translation, PromoCardEventTraitTranslationDTO dto);

    public Map<Localization, PromoCardEventTraitTranslationDTO> translationMapToDtoMap(Map<Localization, PromoCardEventTraitTranslation> translations) {
        return translationMapToDtoMap(translations, this::mapTranslationToDTO);
    }

    public Map<Localization, PromoCardEventTraitTranslation> update(@MappingTarget Map<Localization, PromoCardEventTraitTranslation> translations, Map<Localization, PromoCardEventTraitTranslationDTO> map) {
        return this.update(translations, map, this::update, PromoCardEventTraitTranslation::new);
    }

}
