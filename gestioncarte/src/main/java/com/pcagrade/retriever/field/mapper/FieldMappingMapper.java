package com.pcagrade.retriever.field.mapper;

import com.pcagrade.retriever.PCAMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = PCAMapperConfig.class)
public interface FieldMappingMapper {

    FieldMappingDTO mapToDTO(FieldMapping fieldMapping);

    FieldMapping mapFromDTO(FieldMappingDTO fieldMappingDTO);
}
