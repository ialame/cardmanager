package com.pcagrade.retriever.alert;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class AlertStatusConverter implements AttributeConverter<AlertStatus, String> {

	@Override
	public String convertToDatabaseColumn(AlertStatus localization) {
		if (localization != null) {
			return localization.getCode();
		}
		return null;
	}

	@Override
	public AlertStatus convertToEntityAttribute(String code) {
		if (StringUtils.isNotBlank(code)) {
			return Stream.of(AlertStatus.values())
					.filter(s -> s.getCode().equals(code))
					.findFirst()
					.orElseThrow(IllegalArgumentException::new);
		}
		return null;
	}
}
