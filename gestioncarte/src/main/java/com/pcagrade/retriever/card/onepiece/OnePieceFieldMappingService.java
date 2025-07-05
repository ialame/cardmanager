package com.pcagrade.retriever.card.onepiece;

import com.pcagrade.retriever.field.mapper.FieldMappingService;
import com.pcagrade.retriever.field.mapper.IResolvedMapping;
import com.pcagrade.mason.localization.Localization;
import org.springframework.stereotype.Service;

@Service
public class OnePieceFieldMappingService {

    private final FieldMappingService fieldMappingService;

    public OnePieceFieldMappingService(FieldMappingService fieldMappingService) {
        this.fieldMappingService = fieldMappingService;
    }

    public IResolvedMapping forCode(Localization localization) {
        return fieldMappingService.forFields("sets.onp.code", "sets.onp." + localization.getCode() + ".code");
    }

    public String mapCode(Localization localization, String source) {
        return forCode(localization).map(source);
    }

}
