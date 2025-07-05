package com.pcagrade.retriever.card.set;

import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import com.pcagrade.mason.ulid.jpa.UlidColumnDefinitions;
import com.pcagrade.retriever.card.Card;
import com.pcagrade.retriever.card.set.extraction.status.CardSetExtractionStatus;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.serie.Serie;
import com.pcagrade.retriever.voucher.Voucher;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "card_set")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "discriminator")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CardSet extends AbstractUlidEntity {

	@OneToMany(mappedBy = "translatable", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@MapKey(name = "localization")
	private Map<Localization, CardSetTranslation> translations = new EnumMap<>(Localization.class);

	@ManyToMany(mappedBy = "cardSets", fetch = FetchType.LAZY)
	private List<Card> cards = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id", columnDefinition = UlidColumnDefinitions.DEFAULT_NULL)
	private CardSet parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL)
	private List<CardSet> children = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serie_id")
	private Serie serie;

	@Column(name = "ap_mention")
	private String apMention = "";

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "set", cascade = CascadeType.ALL)
	private List<Voucher> vouchers = new ArrayList<>();

	@OneToOne(mappedBy = "target", orphanRemoval = true, cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private CardSetExtractionStatus extractionStatus;

	@Transient
	public CardSetTranslation getTranslation(Localization localization) {
		return translations.get(localization);
	}

	@Transient
	public List<CardSetTranslation> getTranslations() {
		return List.copyOf(translations.values());
	}

	@Transient
	public void setTranslations(List<CardSetTranslation> translations) {
		translations.forEach(t -> setTranslation(t.getLocalization(), t));
	}

	public void setTranslation(Localization localization, CardSetTranslation translation) {
		if (translation == null) {
			translations.remove(localization);
			return;
		}
		translations.put(localization, translation);
		translation.setTranslatable(this);
		translation.setLocalization(localization);
	}

	public List<Card> getCards() {
		return cards;
	}

	public Serie getSerie() {
		return serie;
	}

	public void setSerie(Serie serie) {
		this.serie = serie;
	}

	public CardSet getParent() {
		return parent;
	}

	public void setParent(CardSet parent) {
		this.parent = parent;
	}

	public List<CardSet> getChildren() {
		return children;
	}

	public List<Voucher> getVouchers() {
		return vouchers;
	}

	public void setVouchers(List<Voucher> vouchers) {
		this.vouchers = vouchers;
	}

	public CardSetExtractionStatus getExtractionStatus() {
		return extractionStatus;
	}

	public void setExtractionStatus(CardSetExtractionStatus extractionStatus) {
		extractionStatus.setTarget(this);
		this.extractionStatus = extractionStatus;
	}


}
