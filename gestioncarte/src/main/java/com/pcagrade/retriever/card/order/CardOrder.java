package com.pcagrade.retriever.card.order;

import com.pcagrade.retriever.card.certification.CardCertification;
import com.pcagrade.retriever.card.order.edit.OrderEdit;
import com.pcagrade.retriever.card.order.history.OrderHistory;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "\"order\"")
public class CardOrder extends AbstractUlidEntity {

	@ManyToMany(mappedBy = "orders")
	private List<CardCertification> certifications;

	@OneToMany(mappedBy = "cardOrder", cascade = CascadeType.ALL)
	private List<OrderEdit> orderEdits;

	@OneToMany(mappedBy = "cardOrder", cascade = CascadeType.ALL)
	private List<OrderHistory> history;


	public List<CardCertification> getCertifications() {
		return certifications;
	}

	public void setCertification(List<CardCertification> certifications) {
		this.certifications = certifications;
	}

	public List<OrderEdit> getOrderEdits() {
		return orderEdits;
	}

	public void setOrderEdits(List<OrderEdit> orderEdits) {
		this.orderEdits = orderEdits;
	}

	public List<OrderHistory> getHistory() {
		return history;
	}

	public void setHistory(List<OrderHistory> history) {
		this.history = history;
	}
}
