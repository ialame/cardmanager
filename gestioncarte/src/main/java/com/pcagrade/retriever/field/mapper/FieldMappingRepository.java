package com.pcagrade.retriever.field.mapper;

import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldMappingRepository extends MasonRepository<FieldMapping, Integer> {
    List<FieldMapping> findAllByField(String field);
}
