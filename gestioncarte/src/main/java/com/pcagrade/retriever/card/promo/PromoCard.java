package com.pcagrade.retriever.card.promo;

import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.localization.jpa.LocalizationAttributeConverter;
import com.pcagrade.mason.localization.jpa.LocalizationColumnDefinitions;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import com.pcagrade.mason.ulid.jpa.UlidColumnDefinitions;
import com.pcagrade.retriever.card.Card;
import com.pcagrade.retriever.card.promo.event.PromoCardEvent;
import com.pcagrade.retriever.card.promo.version.PromoCardVersion;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "promo_card")
@Audited
public class PromoCard extends AbstractUlidEntity implements ILocalized {

	@Column(name = "name")
	private String name;
	@Column(name = "charset", columnDefinition = LocalizationColumnDefinitions.NON_NULL)
	@Convert(converter = LocalizationAttributeConverter.class)
	private Localization localization;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "card_id")
	@Audited
	private Card card;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "promo_card_event_id", columnDefinition = UlidColumnDefinitions.DEFAULT_NULL)
	@Audited
	private PromoCardEvent event;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "version_id", columnDefinition = UlidColumnDefinitions.DEFAULT_NULL)
	@Audited
	private PromoCardVersion version;

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

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

	public PromoCardEvent getEvent() {
		return event;
	}

	public void setEvent(PromoCardEvent event) {
		this.event = event;
	}

	public PromoCardVersion getVersion() {
		return version;
	}

	public void setVersion(PromoCardVersion version) {
		this.version = version;
	}
}
