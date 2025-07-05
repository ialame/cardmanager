package com.pcagrade.retriever.card.tag.type;

import jakarta.annotation.Nonnull;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class CardTagTypeConverter implements AttributeConverter<CardTagType, String>, org.springframework.core.convert.converter.Converter<String, CardTagType> {

    @Override
    public String convertToDatabaseColumn(CardTagType type) {
        if (type != null) {
            return type.getCode();
        }
        return null;
    }

    @Override
    public CardTagType convertToEntityAttribute(String code) {
        if (StringUtils.isNotBlank(code)) {
            return Stream.of(CardTagType.values())
                    .filter(l -> l.getCode().equals(code))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
        return null;
    }

    @Override
    public CardTagType convert(@Nonnull String source) {
        return CardTagType.getByCode(source);
    }
}
