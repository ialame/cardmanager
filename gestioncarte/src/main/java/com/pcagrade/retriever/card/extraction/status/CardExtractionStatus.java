package com.pcagrade.retriever.card.extraction.status;

import com.pcagrade.retriever.card.Card;
import com.pcagrade.retriever.extraction.status.AbstractExtractionStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "j_card_extraction_status", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"target_id"})
})
@Audited
public class CardExtractionStatus extends AbstractExtractionStatus<Card> {
}
