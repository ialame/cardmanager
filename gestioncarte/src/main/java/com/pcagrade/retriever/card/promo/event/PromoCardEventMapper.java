package com.pcagrade.retriever.card.promo.event;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitDTO;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitMapper;
import com.pcagrade.retriever.card.promo.event.translation.PromoCardEventTranslationMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
        uses = {PromoCardEventTranslationMapper.class, PromoCardEventTraitMapper.class})
public abstract class PromoCardEventMapper {

    public abstract PromoCardEventDTO mapToDTO(PromoCardEvent event);

    @Deprecated(forRemoval = true)
    @AfterMapping
    protected void afterMapToDTO(@MappingTarget PromoCardEventDTO dto, PromoCardEvent event) {
        var date = event.getReleaseDate();

        if (date != null) {
            var localDate = date.toLocalDate();

            dto.getTranslations().forEach((k, v) -> {
                if (v.getReleaseDate() == null) {
                    v.setReleaseDate(localDate);
                }
            });
        }
        dto.getTraits().sort(PromoCardEventTraitDTO.COMPARATOR);
    }

    @Mapping(target = "releaseDate", ignore = true)
    @Mapping(target = "traits", ignore = true)
    public abstract void update(@MappingTarget PromoCardEvent event, PromoCardEventDTO dto);

    @Deprecated(forRemoval = true)
    @AfterMapping
    protected void afterUpdate(@MappingTarget PromoCardEvent event, PromoCardEventDTO dto) {
        event.setReleaseDate(null);
    }

}
