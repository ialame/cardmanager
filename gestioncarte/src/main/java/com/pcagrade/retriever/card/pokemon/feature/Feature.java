package com.pcagrade.retriever.card.pokemon.feature;

import com.pcagrade.retriever.card.pokemon.PokemonCard;
import com.pcagrade.retriever.card.pokemon.feature.translation.FeatureTranslation;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "pokemon_card_feature")
@Audited
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Feature extends AbstractUlidEntity {

	@Column(name = "particularite_pca")
	private String pcaName = "";
	private String name = "";
	private boolean visible;
	@Column(name = "ordre")
	private int order;

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "features")
	private List<PokemonCard> cards = new ArrayList<>();

	@OneToMany(mappedBy = "translatable", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@MapKey(name = "localization")
	private Map<Localization, FeatureTranslation> translations = new EnumMap<>(Localization.class);

	@Column(name = "ajout_pca")
	private boolean pcaFeature = false;

	public List<PokemonCard> getCards() {
		return cards;
	}

	public void setCards(List<PokemonCard> cards) {
		this.cards = cards;
	}

	public String getPcaName() {
		return pcaName;
	}

	public void setPcaName(String pcaName) {
		this.pcaName = pcaName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Transient
	public FeatureTranslation getTranslation(Localization localization) {
		return translations.get(localization);
	}

	@Transient
	public List<FeatureTranslation> getTranslations() {
		return List.copyOf(translations.values());
	}

	@Transient
	public void setTranslations(List<FeatureTranslation> translations) {
		this.translations.clear();
		translations.forEach(t -> setTranslation(t.getLocalization(), t));
	}

	public void setTranslation(Localization localization, FeatureTranslation translation) {
		translations.put(localization, translation);
		translation.setTranslatable(this);
		translation.setLocalization(localization);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public boolean isPcaFeature() {
		return pcaFeature;
	}

	public void setPcaFeature(boolean pcaFeature) {
		this.pcaFeature = pcaFeature;
	}

}
