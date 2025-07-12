package com.pcagrade.retriever.card.pokemon.source.dictionary;

import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.localization.jpa.LocalizationAttributeConverter;
import com.pcagrade.mason.localization.jpa.LocalizationColumnDefinitions;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "j_translation_dictionary")
@Immutable
public class DictionaryEntry {

	@Id
	private Integer id;
	
	@Column
	private int count;
	
	@Column(name = "from_locale", columnDefinition = LocalizationColumnDefinitions.NON_NULL)
	@Convert(converter = LocalizationAttributeConverter.class)
	private Localization from;
	
	@Column(name = "to_locale", columnDefinition = LocalizationColumnDefinitions.NON_NULL)
	@Convert(converter = LocalizationAttributeConverter.class)
	private Localization to;
	
	@Column(name = "from_value")
	private String fromValue;
	
	@Column(name = "to_value")
	private String toValue;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Localization getFrom() {
		return from;
	}

	public void setFrom(Localization from) {
		this.from = from;
	}

	public Localization getTo() {
		return to;
	}

	public void setTo(Localization to) {
		this.to = to;
	}

	public String getFromValue() {
		return fromValue;
	}

	public void setFromValue(String fromValue) {
		this.fromValue = fromValue;
	}

	public String getToValue() {
		return toValue;
	}

	public void setToValue(String toValue) {
		this.toValue = toValue;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
