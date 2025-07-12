package com.pcagrade.retriever.card.yugioh.set.translation;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.set.CardSetTranslation;
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
public abstract class YuGiOhSetTranslationMapper extends AbstractTranslationMapper<YuGiOhSetTranslation, YuGiOhSetTranslationDTO> {

    protected YuGiOhSetTranslationMapper() {
        super(YuGiOhSetTranslation.class);
    }

    public abstract YuGiOhSetTranslationDTO mapTranslationToDTO(YuGiOhSetTranslation set);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "translatable", ignore = true)
    @Mapping(target = "labelName", source = "name")
    public abstract YuGiOhSetTranslation mapDTOToTranslation(YuGiOhSetTranslationDTO dto);

    @InheritConfiguration
    public abstract void update(@MappingTarget YuGiOhSetTranslation translation, YuGiOhSetTranslationDTO dto);

    public Map<Localization, YuGiOhSetTranslationDTO> translationListToDtoMap(List<CardSetTranslation> translations) {
        return translationListToDtoMap(translations, this::mapTranslationToDTO);
    }

    public List<CardSetTranslation> dtoMapToTranslationList(Map<Localization, YuGiOhSetTranslationDTO> map) {
        return new ArrayList<>(dtoMapToTranslationList(map, this::mapDTOToTranslation));
    }

    public List<CardSetTranslation> update(@MappingTarget List<CardSetTranslation> translations, Map<Localization, YuGiOhSetTranslationDTO> map) {
        return this.update(translations.stream()
                        .mapMulti(PCAUtils.cast(YuGiOhSetTranslation.class))
                        .toList(), map, this::update, YuGiOhSetTranslation::new)
                .stream()
                .mapMulti(PCAUtils.cast(CardSetTranslation.class))
                .toList();
    }
}
