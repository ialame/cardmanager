package com.pcagrade.retriever.card.set.extraction.status;

import com.pcagrade.retriever.card.set.CardSet;
import com.pcagrade.retriever.extraction.status.AbstractExtractionStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "j_set_extraction_status", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"target_id"})
})
public class CardSetExtractionStatus extends AbstractExtractionStatus<CardSet> {
}
