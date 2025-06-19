package com.pcagrad.magic.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("mag")
public class MagicCardTranslation extends CardTranslation {
    public MagicCardTranslation() {
        super();
    }
}