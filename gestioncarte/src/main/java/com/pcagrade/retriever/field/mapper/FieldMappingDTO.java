package com.pcagrade.retriever.field.mapper;

public record FieldMappingDTO(
        Integer id,
        String field,
        boolean regex,
        String source,
        String value
) { }
