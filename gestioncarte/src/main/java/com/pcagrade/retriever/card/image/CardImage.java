package com.pcagrade.retriever.card.image;

import com.pcagrade.retriever.card.Card;
import com.pcagrade.retriever.card.image.history.CardImageHistory;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import com.pcagrade.retriever.card.pokemon.set.PokemonSet;
import com.pcagrade.retriever.card.set.CardSet;
import com.pcagrade.retriever.image.Image;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "card_image")
public class CardImage extends AbstractUlidEntity {

	@ManyToOne
	@JoinColumn(name = "card_id")
	private Card card;

	@OneToMany(mappedBy = "image", cascade = CascadeType.ALL)
	private List<CardImageHistory> history;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "set_id")
	private CardSet set;

	@Column(name = "langue", nullable = false)
	private String langue;

	@Column(name = "fichier", nullable = false)
	private String fichier;

	@Column(name = "traits", nullable = false, columnDefinition = "longtext")
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, Object> traits;

	@Column(name = "statut", nullable = false)
	private Integer statut;

	@Column(name = "infos", nullable = false, columnDefinition = "longtext")
	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, Object> infos;

	@Column(name = "downloaded_at", nullable = false)
	private Instant downloadedAt;

	@Column(name = "taille_img", length = 50)
	private String tailleImg;

	@Column(name = "cards")
	private String cards;

	@Column(name = "src")
	private String src;

	@Column(name = "localization", nullable = true, length = 5)
	private String localization;

	public List<CardImageHistory> getHistory() {
		return history;
	}

	public void setHistory(List<CardImageHistory> history) {
		this.history = history;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public CardSet getSet() {
		return set;
	}

	public void setSet(CardSet set) {
		this.set = set;
	}

	public String getLangue() {
		return langue;
	}

	public void setLangue(String langue) {
		this.langue = langue;
	}

	public String getFichier() {
		return fichier;
	}

	public void setFichier(String fichier) {
		this.fichier = fichier;
	}

	public Map<String, Object> getTraits() {
		return traits;
	}

	public void setTraits(Map<String, Object> traits) {
		this.traits = traits;
	}

	public Integer getStatut() {
		return statut;
	}

	public void setStatut(Integer statut) {
		this.statut = statut;
	}

	public Instant getDownloadedAt() {
		return downloadedAt;
	}

	public void setDownloadedAt(Instant downloadedAt) {
		this.downloadedAt = downloadedAt;
	}

	public String getTailleImg() {
		return tailleImg;
	}

	public void setTailleImg(String tailleImg) {
		this.tailleImg = tailleImg;
	}

	public String getCards() {
		return cards;
	}

	public void setCards(String cards) {
		this.cards = cards;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getLocalization() {
		return localization;
	}

	public void setLocalization(String localization) {
		this.localization = localization;
	}

	public Map<String, Object> getInfos() {
		return infos;
	}

	public void setInfos(Map<String, Object> infos) {
		this.infos = infos;
	}
}
