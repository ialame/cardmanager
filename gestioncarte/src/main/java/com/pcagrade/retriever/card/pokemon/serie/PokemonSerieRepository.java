package com.pcagrade.retriever.card.pokemon.serie;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonSerieRepository extends MasonRepository<PokemonSerie, Ulid> {

}
