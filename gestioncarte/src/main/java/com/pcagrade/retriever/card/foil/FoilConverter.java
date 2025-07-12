package com.pcagrade.retriever.card.foil;

import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Nonnull;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class FoilConverter implements AttributeConverter<Foil, String>, org.springframework.core.convert.converter.Converter<String, Foil> {

	@Override
	public String convertToDatabaseColumn(Foil foil) {
		if (foil != null) {
			return foil.getName();
		}
		return null;
	}

	@Override
	public Foil convertToEntityAttribute(String name) {
		if (StringUtils.isNotBlank(name)) {
			return Stream.of(Foil.values())
					.filter(l -> l.getName().equals(name))
					.findFirst()
					.orElseThrow(IllegalArgumentException::new);
		}
		return null;
	}

	@Override
	public Foil convert(@Nonnull String source) {
		return Foil.getByName(source);
	}
}
