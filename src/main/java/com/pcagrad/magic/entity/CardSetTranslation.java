package com.pcagrad.magic.entity;

import com.pcagrad.magic.util.Localization;
import com.pcagrad.magic.util.LocalizationConverter;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Entity
@Table(name = "card_set_translation")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "discriminator")
@DiscriminatorValue("bas")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CardSetTranslation  extends AbstractUuidEntity{

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "translatable_id")
	private CardSet translatable;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "label_name")
	private String labelName;

	@Column
	private boolean available;

	@Column(name = "release_date")
	private LocalDateTime releaseDate;

	@Convert(converter = LocalizationConverter.class)
	@Column(name = "locale", length = 5)
	private Localization localization;

}
