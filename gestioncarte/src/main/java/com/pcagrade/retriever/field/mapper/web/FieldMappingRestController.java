package com.pcagrade.retriever.field.mapper.web;

import com.pcagrade.retriever.field.mapper.FieldMappingDTO;
import com.pcagrade.retriever.field.mapper.FieldMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fields/mappings")
public class FieldMappingRestController {

    @Autowired
    private FieldMappingService fieldMappingService;

    @GetMapping
    public List<FieldMappingDTO> getMappings() {
        return fieldMappingService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<FieldMappingDTO> getMapping(@PathVariable int id) {
        return fieldMappingService.get(id);
    }

    @PostMapping
    public int saveMapping(@RequestBody FieldMappingDTO dto) {
        return fieldMappingService.save(dto).id();
    }

    @PutMapping
    public void updateMapping(@RequestBody FieldMappingDTO dto) {
        fieldMappingService.save(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteMapping(@PathVariable int id) {
        fieldMappingService.delete(id);
    }
}
