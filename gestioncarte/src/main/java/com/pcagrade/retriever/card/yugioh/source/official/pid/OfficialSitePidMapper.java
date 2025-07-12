package com.pcagrade.retriever.card.yugioh.source.official.pid;

import com.pcagrade.retriever.PCAMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = PCAMapperConfig.class)
public interface OfficialSitePidMapper {

    OfficialSitePidDTO mapToDTO(OfficialSitePid pid);

    void update(@MappingTarget OfficialSitePid pid, OfficialSitePidDTO dto);
}
