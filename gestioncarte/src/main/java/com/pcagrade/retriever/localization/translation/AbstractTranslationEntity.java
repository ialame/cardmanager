package com.pcagrade.retriever.localization.translation;

import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.localization.jpa.LocalizationAttributeConverter;
import com.pcagrade.mason.localization.jpa.LocalizationColumnDefinitions;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.envers.Audited;

@Audited
@MappedSuperclass
public abstract class AbstractTranslationEntity<T> extends AbstractUlidEntity implements ILocalized {

	@Column(name = "locale", columnDefinition = LocalizationColumnDefinitions.NON_NULL)
	@Convert(converter = LocalizationAttributeConverter.class)
	private Localization localization;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@Audited
	private T translatable;

	@Column
	private String name;

	public T getTranslatable() {
		return translatable;
	}

	public void setTranslatable(T translatable) {
		this.translatable = translatable;
	}

	@Override
	public Localization getLocalization() {
		return localization;
	}

	@Override
	public void setLocalization(Localization localization) {
		this.localization = localization;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
