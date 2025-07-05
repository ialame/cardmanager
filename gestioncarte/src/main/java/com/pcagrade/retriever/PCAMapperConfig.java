package com.pcagrade.retriever;

import com.pcagrade.retriever.date.DateMapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.MapperConfig;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@MapperConfig(componentModel = "spring", 
		nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
		collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		uses = {DateMapper.class})
public class PCAMapperConfig {

}
