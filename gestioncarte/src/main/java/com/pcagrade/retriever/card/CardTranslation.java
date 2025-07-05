package com.pcagrade.retriever.card;

import com.pcagrade.retriever.localization.translation.AbstractTranslationEntity;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Table(name = "card_translation")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "discriminator")
@DiscriminatorValue("bas")
@Audited
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CardTranslation extends AbstractTranslationEntity<Card> {

	private boolean available;

	@OneToOne(mappedBy = "cardTranslation", cascade = CascadeType.ALL)
	@NotAudited
	private CardTranslationSource source;

	@Column(name = "label_name")
	private String labelName;

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public CardTranslationSource getSource() {
		return source;
	}

	public void setSource(CardTranslationSource source) {
		this.source = source;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}
}
