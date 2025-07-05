package com.pcagrade.retriever.card.tag;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.tag.translation.CardTagTranslationMapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = PCAMapperConfig.class,
		collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
		uses = CardTagTranslationMapper.class)
public interface CardTagMapper {

	CardTagDTO mapToDto(CardTag cardTag);

	@Mapping(target = "cards", ignore = true)
	CardTag mapFromDto(CardTagDTO dto);

	List<CardTagDTO> mapToDto(Iterable<CardTag> cardTags);
}
