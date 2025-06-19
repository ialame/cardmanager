package com.pcagrad.magic.entity;

import com.pcagrad.magic.util.Localization;
import com.pcagrad.magic.util.LocalizationConverter;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Entity
@Table(name = "card_translation")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "discriminator")
@DiscriminatorValue("bas")
public class CardTranslation  extends AbstractUuidEntity{

	private boolean available;

	@Convert(converter = LocalizationConverter.class)
	@Column(name = "locale", length = 5)
	private Localization localization;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Card translatable;

	@Column
	private String name;


	@Column(name = "label_name")
	private String labelName;

}
