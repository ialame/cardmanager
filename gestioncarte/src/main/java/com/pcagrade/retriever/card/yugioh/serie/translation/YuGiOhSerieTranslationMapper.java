package com.pcagrade.retriever.card.yugioh.serie.translation;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.AbstractTranslationMapper;
import com.pcagrade.retriever.serie.SerieTranslation;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(config = PCAMapperConfig.class)
public abstract class YuGiOhSerieTranslationMapper extends AbstractTranslationMapper<SerieTranslation, YuGiOhSerieTranslationDTO> {

    protected YuGiOhSerieTranslationMapper() {
        super(SerieTranslation.class);
    }

    public abstract YuGiOhSerieTranslationDTO mapTranslationToDTO(SerieTranslation serie);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "translatable", ignore = true)
    public abstract SerieTranslation mapDTOToTranslation(YuGiOhSerieTranslationDTO dto);

    @InheritConfiguration
    public abstract void update(@MappingTarget SerieTranslation translation, YuGiOhSerieTranslationDTO dto);

    public Map<Localization, YuGiOhSerieTranslationDTO> translationListToDtoMap(List<SerieTranslation> translations) {
        return translationListToDtoMap(translations, this::mapTranslationToDTO);
    }

    public List<SerieTranslation> dtoMapToTranslationList(Map<Localization, YuGiOhSerieTranslationDTO> map) {
        return new ArrayList<>(dtoMapToTranslationList(map, this::mapDTOToTranslation));
    }

    public List<SerieTranslation> update(@MappingTarget List<SerieTranslation> translations, Map<Localization, YuGiOhSerieTranslationDTO> map) {
        return this.update(translations.stream()
                        .mapMulti(PCAUtils.cast(SerieTranslation.class))
                        .toList(), map, this::update, SerieTranslation::new)
                .stream()
                .mapMulti(PCAUtils.cast(SerieTranslation.class))
                .toList();
    }
}
