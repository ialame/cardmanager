package com.pcagrade.retriever.card.pokemon.source.bulbapedia.expansion;

import com.pcagrade.retriever.PCAMapperConfig;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = PCAMapperConfig.class)
public interface ExpansionBulbapediaMapper {

	@Mapping(target = "setId", source = "set.id")
	@Mapping(target = "serieId", source = "set.serie.id")
	@Mapping(target = "promo", source = "set.promo")
	ExpansionBulbapediaDTO mapToDto(ExpansionBulbapedia expansion);
	
	@InheritInverseConfiguration
	@Mapping(target = "charset", ignore = true)
	@Mapping(target = "set", ignore = true)
	void update(@MappingTarget ExpansionBulbapedia expansion, ExpansionBulbapediaDTO dto);

	List<ExpansionBulbapediaDTO> mapToDto(Iterable<ExpansionBulbapedia> expansions);
}
