package com.pcagrade.retriever.localization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.PCAMapperConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

@Mapper(config = PCAMapperConfig.class)
public abstract class LocalizationMapper {

    private static final Logger LOGGER = LogManager.getLogger(LocalizationMapper.class);

    @Autowired
    private ObjectMapper mapper;

    public List<Localization> mapLocalizations(String localizations) {
        if (StringUtils.isBlank(localizations)) {
            return Collections.emptyList();
        }
        try {
            return mapper.readValue(localizations, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            LOGGER.error(() -> "Error while parsing localizations: {}" + localizations, e);
            return Collections.emptyList();
        }
    }

    public String mapLocalizations(List<Localization> localizations) {
        if (CollectionUtils.isEmpty(localizations)) {
            return "";
        }
        try {
            return mapper.writeValueAsString(localizations);
        } catch (JsonProcessingException e) {
            LOGGER.error(() -> "Error while serializing localizations: " + localizations, e);
            return "";
        }
    }
}
