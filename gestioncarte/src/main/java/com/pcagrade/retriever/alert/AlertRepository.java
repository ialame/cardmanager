package com.pcagrade.retriever.alert;

import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlertRepository extends MasonRepository<Alert, Integer> {

	Optional<Alert> findFirstByTypeIgnoreCaseAndParams(String type, String params);
	
}
