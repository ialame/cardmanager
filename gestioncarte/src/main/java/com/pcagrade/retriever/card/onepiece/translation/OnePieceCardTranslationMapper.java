package com.pcagrade.retriever.card.onepiece.translation;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.CardTranslation;
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
public abstract class OnePieceCardTranslationMapper extends AbstractTranslationMapper<CardTranslation, OnePieceCardTranslationDTO> {

    protected OnePieceCardTranslationMapper() {
        super(CardTranslation.class);
    }

    public abstract OnePieceCardTranslationDTO mapTranslationToDTO(CardTranslation card);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "translatable", ignore = true)
    public abstract CardTranslation mapDTOToTranslation(OnePieceCardTranslationDTO dto);

    @InheritConfiguration
    public abstract void update(@MappingTarget CardTranslation translation, OnePieceCardTranslationDTO dto);

    public Map<Localization, OnePieceCardTranslationDTO> translationListToDtoMap(List<CardTranslation> translations) {
        return translationListToDtoMap(translations, this::mapTranslationToDTO);
    }

    public List<CardTranslation> dtoMapToTranslationList(Map<Localization, OnePieceCardTranslationDTO> map) {
        return new ArrayList<>(dtoMapToTranslationList(map, this::mapDTOToTranslation));
    }

    public List<CardTranslation> update(@MappingTarget List<CardTranslation> translations, Map<Localization, OnePieceCardTranslationDTO> map) {
        return this.update(translations, map, this::update, CardTranslation::new);
    }
}
