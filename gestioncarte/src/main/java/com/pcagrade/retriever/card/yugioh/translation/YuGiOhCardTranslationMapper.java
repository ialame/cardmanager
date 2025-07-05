package com.pcagrade.retriever.card.yugioh.translation;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.CardTranslation;
import com.pcagrade.retriever.card.yugioh.source.official.OfficialSiteCard;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.AbstractTranslationMapper;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Mapper(config = PCAMapperConfig.class)
public abstract class YuGiOhCardTranslationMapper extends AbstractTranslationMapper<YuGiOhCardTranslation, YuGiOhCardTranslationDTO> {

    protected YuGiOhCardTranslationMapper() {
        super(YuGiOhCardTranslation.class);
    }

    public abstract YuGiOhCardTranslationDTO mapTranslationToDTO(YuGiOhCardTranslation card);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "translatable", ignore = true)
    @Mapping(target = "labelName", source = "name")
    public abstract YuGiOhCardTranslation mapDTOToTranslation(YuGiOhCardTranslationDTO dto);

    @InheritConfiguration
    public abstract void update(@MappingTarget YuGiOhCardTranslation translation, YuGiOhCardTranslationDTO dto);

    public Map<Localization, YuGiOhCardTranslationDTO> translationListToDtoMap(List<CardTranslation> translations) {
        return translationListToDtoMap(translations, this::mapTranslationToDTO);
    }

    public List<CardTranslation> dtoMapToTranslationList(Map<Localization, YuGiOhCardTranslationDTO> map) {
        return new ArrayList<>(dtoMapToTranslationList(map, this::mapDTOToTranslation));
    }

    public List<CardTranslation> update(@MappingTarget List<CardTranslation> translations, Map<Localization, YuGiOhCardTranslationDTO> map) {
        return this.update(translations.stream()
                        .mapMulti(PCAUtils.cast(YuGiOhCardTranslation.class))
                        .toList(), map, this::update, YuGiOhCardTranslation::new)
                .stream()
                .mapMulti(PCAUtils.cast(CardTranslation.class))
                .toList();
    }
}
