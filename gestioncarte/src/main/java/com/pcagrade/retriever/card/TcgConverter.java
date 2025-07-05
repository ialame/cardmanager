package com.pcagrade.retriever.card;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.annotation.Nonnull;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Component
@Converter(autoApply = true)
public class TcgConverter implements AttributeConverter<TradingCardGame, String>, org.springframework.core.convert.converter.Converter<String, TradingCardGame> {

    @Override
    public TradingCardGame convert(@Nonnull String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        return TradingCardGame.parse(source);
    }

    @Override
    public String convertToDatabaseColumn(TradingCardGame tcg) {
        return tcg != null ? tcg.getCode() : null;
    }

    @Override
    public TradingCardGame convertToEntityAttribute(String source) {
        return convert(source);
    }
}
