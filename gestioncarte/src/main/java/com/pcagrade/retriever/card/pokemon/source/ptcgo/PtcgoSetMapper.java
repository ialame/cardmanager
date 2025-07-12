package com.pcagrade.retriever.card.pokemon.source.ptcgo;

import com.pcagrade.retriever.PCAMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = PCAMapperConfig.class)
public interface PtcgoSetMapper {

	PtcgoSetDTO mapToDTO(PtcgoSet set);

}
