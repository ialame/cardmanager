package com.pcagrade.mason.localization;

import jakarta.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;

public class LocalizationConverter implements Converter<String, Localization> {

	@Override
	public Localization convert(@Nonnull String source) {
		return Localization.getByCode(source);
	}
}
