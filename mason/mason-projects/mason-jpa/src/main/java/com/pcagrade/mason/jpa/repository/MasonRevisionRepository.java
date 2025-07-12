package com.pcagrade.mason.jpa.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.history.RevisionRepository;

import java.io.Serializable;

@NoRepositoryBean
public interface MasonRevisionRepository<T, I extends Serializable> extends InternalMasonRevisionRepository<T, I>, RevisionRepository<T, I, Integer> {

}
