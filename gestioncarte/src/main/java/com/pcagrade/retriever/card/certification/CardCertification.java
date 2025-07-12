package com.pcagrade.retriever.card.certification;

import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.localization.jpa.LocalizationAttributeConverter;
import com.pcagrade.mason.localization.jpa.LocalizationColumnDefinitions;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import com.pcagrade.retriever.card.Card;
import com.pcagrade.retriever.card.certification.comment.CardCertificationComment;
import com.pcagrade.retriever.card.certification.history.CardCertificationHistory;
import com.pcagrade.retriever.card.certification.searched.SearchedCertification;
import com.pcagrade.retriever.card.order.CardOrder;
import com.pcagrade.retriever.card.order.edit.OrderEdit;
import com.pcagrade.retriever.card.order.history.OrderHistory;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "card_certification")
public class CardCertification extends AbstractUlidEntity implements ILocalized {

	@ManyToOne
	@JoinColumn(name = "card_id")
	private Card card;

	@OneToMany(mappedBy = "certification", cascade = CascadeType.ALL)
	private List<CardCertificationHistory> history;

	@OneToMany(mappedBy = "certification", cascade = CascadeType.ALL)
	private List<CardCertificationComment> comments;

	@OneToMany(mappedBy = "certification", cascade = CascadeType.ALL)
	private List<SearchedCertification> search;

	@ManyToMany
	@JoinTable(name = "card_certification_order", joinColumns = @JoinColumn(name = "card_certification_id"), inverseJoinColumns = @JoinColumn(name = "order_id"))
	private List<CardOrder> orders;

	@OneToMany(mappedBy = "certification", cascade = CascadeType.ALL)
	private List<OrderEdit> orderEdits;

	@OneToMany(mappedBy = "certification", cascade = CascadeType.ALL)
	private List<OrderHistory> orderHistory;

	@Column(name = "langue", columnDefinition = LocalizationColumnDefinitions.NON_NULL)
	@Convert(converter = LocalizationAttributeConverter.class)
	private Localization localization;

	@Column(name = "code_barre")
	private String barcode;

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public List<CardCertificationHistory> getHistory() {
		return history;
	}

	public void setHistory(List<CardCertificationHistory> history) {
		this.history = history;
	}

	public List<CardOrder> getOrders() {
		return orders;
	}

	public void setOrder(List<CardOrder> orders) {
		this.orders = orders;
	}

	public List<CardCertificationComment> getComments() {
		return comments;
	}

	public void setComments(List<CardCertificationComment> comments) {
		this.comments = comments;
	}

	public List<OrderEdit> getOrderEdits() {
		return orderEdits;
	}

	public void setOrderEdits(List<OrderEdit> orderEdits) {
		this.orderEdits = orderEdits;
	}

	public List<OrderHistory> getOrderHistory() {
		return orderHistory;
	}

	public void setOrderHistory(List<OrderHistory> orderHistory) {
		this.orderHistory = orderHistory;
	}

	public List<SearchedCertification> getSearch() {
		return search;
	}

	public void setSearch(List<SearchedCertification> search) {
		this.search = search;
	}

	@Override
	public Localization getLocalization() {
		return localization;
	}

	@Override
	public void setLocalization(Localization localization) {
		this.localization = localization;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
}
