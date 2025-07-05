package com.pcagrade.retriever.card.yugioh.source.yugipedia.set;

import com.pcagrade.retriever.PCAMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class)
public interface YugipediaSetMapper {

    YugipediaSetDTO mapToDTO(YugipediaSet pid);

    void update(@MappingTarget YugipediaSet pid, YugipediaSetDTO dto);
}
