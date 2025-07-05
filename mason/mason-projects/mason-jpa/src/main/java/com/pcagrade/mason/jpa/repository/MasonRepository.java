package com.pcagrade.mason.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Supplier;

@NoRepositoryBean
public interface MasonRepository<T, I extends Serializable> extends JpaRepository<T, I>, JpaSpecificationExecutor<T> {

	default Optional<T> findByNullableId(I id) {
		if (id == null) {
			return Optional.empty();
		}
		return findById(id);
	}

	default T getOrCreate(I id, Supplier<T> supplier) {
		return findByNullableId(id).orElseGet(supplier);
	}

	String getTableName();
}
