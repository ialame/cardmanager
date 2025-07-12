package com.pcagrade.retriever.card.artist;

import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface CardArtistRepository extends MasonRepository<CardArtist, Integer> {

    Optional<CardArtist> findFirstByName(String name);

}
