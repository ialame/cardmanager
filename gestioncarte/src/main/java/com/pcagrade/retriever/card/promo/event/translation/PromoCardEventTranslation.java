package com.pcagrade.retriever.card.promo.event.translation;

import com.pcagrade.retriever.card.promo.event.PromoCardEvent;
import com.pcagrade.retriever.localization.translation.AbstractTranslationEntity;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Table(name = "promo_card_event_translation")
@Audited
@Cacheable
public class PromoCardEventTranslation extends AbstractTranslationEntity<PromoCardEvent> {

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @Column(name = "label_name")
    private String labelName;

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
