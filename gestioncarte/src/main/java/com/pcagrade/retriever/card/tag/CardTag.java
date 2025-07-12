package com.pcagrade.retriever.card.tag;

import com.pcagrade.retriever.card.Card;
import com.pcagrade.retriever.card.tag.translation.CardTagTranslation;
import com.pcagrade.retriever.card.tag.type.CardTagType;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.envers.Audited;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "card_tag")
@Audited
public class CardTag extends AbstractUlidEntity {

    @ManyToMany(mappedBy = "cardTags")
	private Set<Card> cards = new HashSet<>();

	@Column
	private CardTagType type;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "translatable", cascade = CascadeType.ALL)
	@MapKey(name = "localization")
	private Map<Localization, CardTagTranslation> translations = new EnumMap<>(Localization.class);

	public Set<Card> getCards() {
		return cards;
	}

	public void setCards(Set<Card> cards) {
		this.cards = cards;
	}

	public CardTagType getType() {
		return type;
	}

	public void setType(CardTagType type) {
		this.type = type;
	}

	public Map<Localization, CardTagTranslation> getTranslations() {
		return translations;
	}

	public void setTranslations(Map<Localization, CardTagTranslation> translations) {
		this.translations = translations;
	}
}
