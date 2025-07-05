package com.pcagrade.retriever.card.pokemon.bracket;

import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.localization.jpa.LocalizationAttributeConverter;
import com.pcagrade.mason.localization.jpa.LocalizationColumnDefinitions;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import com.pcagrade.retriever.card.pokemon.PokemonCard;
import com.pcagrade.retriever.card.pokemon.bracket.translation.BracketTranslation;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "crochet")
@Audited
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Bracket extends AbstractUlidEntity implements ILocalized {

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "translatable", cascade = CascadeType.ALL)
	@MapKey(name = "localization")
	@Audited
	private Map<Localization, BracketTranslation> translations = new EnumMap<>(Localization.class);

	@Column(name = "nom")
	private String name;

	@Column(name = "charset", columnDefinition = LocalizationColumnDefinitions.NON_NULL)
	@Convert(converter = LocalizationAttributeConverter.class)
	private Localization localization;

	@Column(name = "isCrochetPCA")
	private boolean pcaBracket;

	@Column(name = "is_card_gras")
	private boolean bold;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH }, mappedBy = "brackets")
	private List<PokemonCard> cards = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Localization getLocalization() {
		return localization;
	}

	@Override
	public void setLocalization(Localization localization) {
		this.localization = localization;
	}

	public List<PokemonCard> getCards() {
		return cards;
	}

	public void setCards(List<PokemonCard> cards) {
		this.cards = cards;
	}

	public boolean isPcaBracket() {
		return pcaBracket;
	}

	public void setPcaBracket(boolean pcaBracket) {
		this.pcaBracket = pcaBracket;
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	@Transient
	public BracketTranslation getTranslation(Localization localization) {
		return translations.get(localization);
	}

	@Transient
	public List<BracketTranslation> getTranslations() {
		return List.copyOf(translations.values());
	}

	@Transient
	public void setTranslations(List<BracketTranslation> translations) {
		this.translations.clear();
		translations.forEach(t -> setTranslation(t.getLocalization(), t));
	}

	public void setTranslation(Localization localization, BracketTranslation translation) {
		translations.put(localization, translation);
		translation.setTranslatable(this);
		translation.setLocalization(localization);
	}
}
