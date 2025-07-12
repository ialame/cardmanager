package com.pcagrade.mason.jpa.revision;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import java.util.Date;

@Entity
@Table(name = "revision_info")
@RevisionEntity(MasonRevisionListener.class)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RevisionInfo {

	@Id
	@RevisionNumber
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@RevisionTimestamp
	@Column(name = "modification_date")
	private Date modificationDate;

	@Column
	private String author;

	@Column(columnDefinition = "LONGTEXT")
	private String message;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}
