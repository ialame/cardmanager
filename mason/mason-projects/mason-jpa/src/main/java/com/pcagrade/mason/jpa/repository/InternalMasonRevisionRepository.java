package com.pcagrade.mason.jpa.repository;

import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;

@NoRepositoryBean
public interface InternalMasonRevisionRepository<T, I extends Serializable> extends MasonRepository<T, I>, RevisionAware {

    Optional<T> findAtTime(I id, Instant instant);
}
