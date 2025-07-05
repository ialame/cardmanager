package com.pcagrade.retriever.card.image.history;

import com.pcagrade.retriever.card.image.CardImage;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "card_image_history")
public class CardImageHistory extends AbstractUlidEntity {

	@ManyToOne
	@JoinColumn(name = "card_image_id")
	private CardImage image;

	public CardImage getImage() {
		return image;
	}

	public void setImage(CardImage image) {
		this.image = image;
	}

}
