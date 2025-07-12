package com.pcagrade.retriever.card.yugioh;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.extraction.status.CardExtractionStatusMapper;
import com.pcagrade.retriever.card.yugioh.translation.YuGiOhCardTranslationMapper;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.AfterMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

@Mapper(config = PCAMapperConfig.class,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
        uses = {YuGiOhCardTranslationMapper.class, CardExtractionStatusMapper.class})
public abstract class YuGiOhCardMapper {

    private static final Logger LOGGER = LogManager.getLogger(YuGiOhCardMapper.class);

    @Autowired
    private ObjectMapper mapper;

    @Mapping(target = "artist", source = "artist.name")
    public abstract YuGiOhCardDTO mapToDTO(YuGiOhCard card);

    @AfterMapping
    protected void afterMapping(@MappingTarget YuGiOhCardDTO dto, YuGiOhCard card) {
        dto.setSetIds(card.getCardSets().stream()
                .map(AbstractUlidEntity::getId)
                .toList());
    }

    @Mapping(target = "artist", ignore = true)
    public abstract void update(@MappingTarget YuGiOhCard card, YuGiOhCardDTO dto);

    protected List<String> mapTypes(String types) {
        if (StringUtils.isBlank(types)) {
            return Collections.emptyList();
        }
        try {
            return mapper.readValue(types, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            LOGGER.error(() -> "Error while parsing types: {}" + types, e);
            return Collections.emptyList();
        }
    }

    protected String mapTypes(List<String> localizations) {
        if (CollectionUtils.isEmpty(localizations)) {
            return "";
        }
        try {
            return mapper.writeValueAsString(localizations);
        } catch (JsonProcessingException e) {
            LOGGER.error(() -> "Error while serializing types: " + localizations, e);
            return "";
        }
    }
}
