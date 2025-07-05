package com.pcagrade.retriever.card;

import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import com.pcagrade.mason.ulid.jpa.UlidColumnDefinitions;
import com.pcagrade.retriever.card.artist.CardArtist;
import com.pcagrade.retriever.card.certification.CardCertification;
import com.pcagrade.retriever.card.extraction.status.CardExtractionStatus;
import com.pcagrade.retriever.card.image.CardImage;
import com.pcagrade.retriever.card.price.AveragePrice;
import com.pcagrade.retriever.card.promo.PromoCard;
import com.pcagrade.retriever.card.saved.SavedCard;
import com.pcagrade.retriever.card.set.CardSet;
import com.pcagrade.retriever.card.tag.CardTag;
import com.pcagrade.retriever.cart.item.CartItem;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "card")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "discriminator")
@Audited
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Card extends AbstractUlidEntity {
	@Transient
	private String name = "";
	@Transient
	private String url = "";
	@Transient
	private String imageUrl = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "translatable", cascade = CascadeType.ALL)
	@MapKey(name = "localization")
	@Audited
	private Map<Localization, CardTranslation> translations = new EnumMap<>(Localization.class);

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "card_card_set", joinColumns = @JoinColumn(name = "card_id"), inverseJoinColumns = @JoinColumn(name = "card_set_id"))
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private Set<CardSet> cardSets = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "card_card_tag", joinColumns = @JoinColumn(name = "card_id"), inverseJoinColumns = @JoinColumn(name = "card_tag_id"))
	@Audited
	private Set<CardTag> cardTags = new HashSet<>();

	@OneToOne(mappedBy = "card", cascade = CascadeType.ALL)
	@NotAudited // TODO remove
	private AveragePrice price;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "card", cascade = CascadeType.ALL)
	@NotAudited
	private Set<CartItem> items = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "card", cascade = CascadeType.ALL)
	@NotAudited
	private Set<CardCertification> certifications = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "card", cascade = CascadeType.ALL)
	@NotAudited // TODO remove
	private Set<CardImage> images = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "card", cascade = CascadeType.ALL)
	@NotAudited // TODO remove
	private Set<SavedCard> savedCards = new HashSet<>();

	@OneToOne(mappedBy = "target", cascade = CascadeType.ALL)
	private CardExtractionStatus extractionStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "card_artist", columnDefinition = UlidColumnDefinitions.DEFAULT_NULL)
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private CardArtist artist;

	@Column(name = "num")
	private String number = "";

	@Lob
	@Column(name = "attributes", columnDefinition = "LONGTEXT")
	private String attributes = "{\"reverse\": 0, \"edition\": 1, \"shadowless\": 0}";

	@Lob
	@Column(name = "allowed_notes", columnDefinition = "LONGTEXT")
	private String allowedNotes = "[]";

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "card", cascade = CascadeType.ALL)
	private Set<PromoCard> promoCards = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "card_promosused",
			joinColumns = @JoinColumn(name = "card_id"),
			inverseJoinColumns = @JoinColumn(name = "promo_card_id"))
	private Set<PromoCard> promoUsed = new HashSet<>();

	@Column(name = "image_id")
	private Integer imageId;

	@Transient
	public CardTranslation getTranslation(Localization localization) {
		return translations.get(localization);
	}

	@Transient
	public List<CardTranslation> getTranslations() {
		return List.copyOf(translations.values());
	}

	@Transient
	public void setTranslations(List<CardTranslation> translations) {
		translations.forEach(translation -> setTranslation(translation.getLocalization(), translation));
	}

	@Transient
	public Map<Localization, CardTranslation> getTranslationMap() {
		return translations;
	}

	@Transient
	public void setTranslation(Localization localization, CardTranslation translation) {
		if (translation != null) {
			translations.put(localization, translation);
			translation.setTranslatable(this);
			translation.setLocalization(localization);
		} else {
			translations.remove(localization);
		}
	}

	public Set<CardSet> getCardSets() {
		return cardSets;
	}

	public Set<CardTag> getCardTags() {
		return cardTags;
	}

	public AveragePrice getPrice() {
		return price;
	}

	public void setPrice(AveragePrice price) {
		if (price != null) {
			price.setCard(this);
		}
		this.price = price;
	}

	public Set<CartItem> getItems() {
		return items;
	}

	public void setItems(Set<CartItem> items) {
		this.items = items;
	}

	public Set<CardCertification> getCertifications() {
		return certifications;
	}

	public void setCertification(Set<CardCertification> certifications) {
		this.certifications = certifications;
	}

	public Set<CardImage> getImages() {
		return images;
	}

	public void setImages(Set<CardImage> images) {
		this.images = images;
	}

	public Set<SavedCard> getSavedCards() {
		return savedCards;
	}

	public void setSavedCards(Set<SavedCard> savedCards) {
		this.savedCards = savedCards;
	}

	public CardExtractionStatus getExtractionStatus() {
		return extractionStatus;
	}

	public void setExtractionStatus(CardExtractionStatus extractionStatus) {
		extractionStatus.setTarget(this);
		this.extractionStatus = extractionStatus;
	}

	public CardArtist getArtist() {
		return artist;
	}

	public void setArtist(CardArtist artist) {
		this.artist = artist;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Set<PromoCard> getPromoCards() {
		return promoCards;
	}

	public void setPromoCards(Set<PromoCard> promoCards) {
		this.promoCards = promoCards;
	}

	public Set<PromoCard> getPromoUsed() {
		return promoUsed;
	}

	public void setPromoUsed(Set<PromoCard> promoUsed) {
		this.promoUsed = promoUsed;
	}

	public Integer getImageId() {
		return imageId;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}
}
