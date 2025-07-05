package com.pcagrade.retriever.merge.history;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import com.pcagrade.mason.jpa.repository.RevisionAware;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface MergeHistoryRepository extends MasonRepository<MergeHistory, Integer>, RevisionAware {

	Optional<MergeHistory> findByTableAndFrom(String table, Ulid cardId);
	List<MergeHistory> findAllByTableAndTo(String table, Ulid cardId);

}
