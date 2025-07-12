package com.pcagrade.mason.jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.io.Serializable;

public class SimpleMasonRepository<T, I extends Serializable> extends SimpleJpaRepository<T, I> implements MasonRepository<T, I> {

	private final EntityManager entityManager;

	public SimpleMasonRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}

	@Override
	public String getTableName() {
		var meta = entityManager.getMetamodel();
		var domainClass = getDomainClass();
		var entityType = meta.entity(domainClass);
		var table = domainClass.getAnnotation(Table.class);

		return table == null ? entityType.getName() : table.name();
	}
}
