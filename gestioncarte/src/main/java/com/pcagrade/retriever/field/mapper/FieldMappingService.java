package com.pcagrade.retriever.field.mapper;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FieldMappingService {

    @Autowired
    private FieldMappingRepository fieldMappingRepository;
    @Autowired
    private FieldMappingMapper fieldMappingMapper;

    public List<FieldMappingDTO> getAll() {
        return fieldMappingRepository.findAll().stream()
                .map(fieldMappingMapper::mapToDTO)
                .toList();
    }

    public Optional<FieldMappingDTO> get(int id) {
        return fieldMappingRepository.findById(id)
                .map(fieldMappingMapper::mapToDTO);
    }

    public FieldMappingDTO save(FieldMappingDTO dto) {
        return fieldMappingMapper.mapToDTO(fieldMappingRepository.save(fieldMappingMapper.mapFromDTO(dto)));
    }

    public void delete(int id) {
        fieldMappingRepository.deleteById(id);
    }

    public String map(String field, String source) {
        if (StringUtils.isAnyBlank(field, source)) {
            return source;
        }

        var result = source;
        var fields = fieldMappingRepository.findAllByField(field);

        for (var fieldMapping : fields) {
            if (fieldMapping.isRegex()) {
                result = RegExUtils.replaceAll(result, fieldMapping.getSource(), fieldMapping.getValue());
            } else {
                result = StringUtils.replace(result, fieldMapping.getSource(), fieldMapping.getValue());
            }
        }
        return result;
    }

    public String map(Iterable<String> fields, String source) {
        var result = source;

        for (var field : fields) {
            result = map(field, result);
        }
        return result;
    }

    public String map(String[] fields, String source) {
        return map(List.of(fields), source);
    }

    @Cacheable("fieldMapping")
    public IResolvedMapping forFields(String... fields) {
        return source -> map(fields, source);
    }
}
