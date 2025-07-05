package com.pcagrade.mason.localization;

import jakarta.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;


public class LocalizationGroupConverter implements Converter<String, Localization.Group> {

    @Override
    public Localization.Group convert(@Nonnull String source) {
        return Localization.Group.getByCode(source);
    }
}
