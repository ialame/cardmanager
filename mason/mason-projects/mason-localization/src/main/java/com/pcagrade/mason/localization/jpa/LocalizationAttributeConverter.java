package com.pcagrade.mason.localization.jpa;

import com.pcagrade.mason.localization.Localization;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LocalizationAttributeConverter implements AttributeConverter<Localization, String> {

    @Override
    public String convertToDatabaseColumn(Localization localization) {
        if (localization != null) {
            return localization.getCode();
        }
        return null;
    }

    @Override
    public Localization convertToEntityAttribute(String code) {
        return Localization.getByCode(code);
    }
}
