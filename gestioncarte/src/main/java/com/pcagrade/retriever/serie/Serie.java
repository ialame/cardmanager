package com.pcagrade.retriever.serie;

import com.pcagrade.retriever.card.set.CardSet;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "serie")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "discriminator")
@DiscriminatorValue("bas")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Serie extends AbstractUlidEntity {

	@OneToMany(mappedBy = "translatable", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@MapKey(name = "localization")
	private Map<Localization, SerieTranslation> translations = new EnumMap<>(Localization.class);


	@OneToMany(mappedBy = "serie", fetch = FetchType.LAZY)
	private List<CardSet> sets;

	@Transient
	public SerieTranslation getTranslation(Localization localization) {
		return translations.get(localization);
	}

	@Transient
	public List<SerieTranslation> getTranslations() {
		return List.copyOf(translations.values());
	}

	@Transient
	public void setTranslations(List<SerieTranslation> translations) {
		translations.forEach(t -> setTranslation(t.getLocalization(), t));
	}

	public void setTranslation(Localization localization, SerieTranslation translation) {
		translations.put(localization, translation);
		translation.setTranslatable(this);
		translation.setLocalization(localization);
	}

	public List<CardSet> getSets() {
		return sets;
	}

	public void setSets(List<CardSet> sets) {
		this.sets = sets;
	}
}
