package com.pcagrade.mason.jpa.repository;

import jakarta.persistence.EntityManager;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

public class SimpleMasonRevisionRepository<T, I extends Serializable> extends SimpleMasonRepository<T, I> implements InternalMasonRevisionRepository<T, I> {

	private final EntityManager entityManager;

	public SimpleMasonRevisionRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}

	protected AuditReader getAuditReader() {
		return AuditReaderFactory.get(entityManager);
	}

	@Override
	public int getLastRevision() {
		return this.getAuditReader().getRevisionNumberForDate(new Date()).intValue();
	}

	@Override
	@Transactional
	public Optional<T> findAtTime(I id, Instant instant) {
		return Optional.ofNullable(getAuditReader().find(getDomainClass(), id, Date.from(instant)));
	}
}
