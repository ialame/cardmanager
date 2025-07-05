package com.pcagrade.retriever.card.pokemon.bracket;

import com.pcagrade.retriever.PCAMapperConfig;
import com.pcagrade.retriever.card.pokemon.bracket.translation.BracketTranslationMapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = PCAMapperConfig.class,
		collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
		uses = BracketTranslationMapper.class)
public interface BracketMapper {

	BracketDTO mapToDto(Bracket bracket);

	@Mapping(target = "cards", ignore = true)
	@Mapping(target = "pcaBracket", ignore = true)
	Bracket mapFromDto(BracketDTO dto);

	@InheritConfiguration
	void update(@MappingTarget Bracket bracket, BracketDTO dto);

	List<BracketDTO> mapToDto(Iterable<Bracket> brackets);

	default String getBracketName(BracketDTO bracket) {
		return bracket.getName();
	}

}
