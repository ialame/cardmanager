package com.pcagrade.retriever.card.yugioh;

import com.pcagrade.retriever.field.mapper.FieldMappingService;
import com.pcagrade.retriever.field.mapper.IResolvedMapping;
import com.pcagrade.mason.localization.Localization;
import org.springframework.stereotype.Service;

@Service
public class YuGiOhFieldMappingService {

    private static final String PREFIX = "cards.ygh.";

    private final FieldMappingService fieldMappingService;

    public YuGiOhFieldMappingService(FieldMappingService fieldMappingService) {
        this.fieldMappingService = fieldMappingService;
    }

    public IResolvedMapping forRarity(Localization localization) {
        return forField(localization, "rarity");
    }

    public String mapRarity(Localization localization, String source) {
        return forRarity(localization).map(source);
    }

    public IResolvedMapping forNumber(Localization localization) {
        return forField(localization, "number");
    }

    public String mapNumber(Localization localization, String source) {
        return forNumber(localization).map(source);
    }

    private IResolvedMapping forField(Localization localization, String field) {
        return fieldMappingService.forFields(PREFIX + field, PREFIX + localization.getCode() + "." + field);
    }
}
