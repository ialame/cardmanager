package com.pcagrad.magic.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("mag")
public class MagicCardTranslation extends CardTranslation {
    public MagicCardTranslation() {
        super();
    }
}