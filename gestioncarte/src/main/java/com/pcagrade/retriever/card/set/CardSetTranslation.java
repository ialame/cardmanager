package com.pcagrade.retriever.card.set;

import com.pcagrade.retriever.localization.translation.AbstractTranslationEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "card_set_translation")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "discriminator")
@DiscriminatorValue("bas")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CardSetTranslation extends AbstractTranslationEntity<CardSet> {

	@Column
	private boolean available;

	@Column(name = "release_date")
	private LocalDateTime releaseDate;

	@Column(name = "label_name")
	private String labelName;

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public LocalDateTime getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(LocalDateTime releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}
}
