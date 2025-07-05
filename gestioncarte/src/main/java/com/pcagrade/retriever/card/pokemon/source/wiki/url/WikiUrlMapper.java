package com.pcagrade.retriever.card.pokemon.source.wiki.url;

import com.pcagrade.retriever.PCAMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class)
public interface WikiUrlMapper {

    WikiUrlDTO mapToDto(WikiUrl wikiUrl);

    void update(@MappingTarget WikiUrl wikiUrl, WikiUrlDTO wikiUrlDTO);
}
