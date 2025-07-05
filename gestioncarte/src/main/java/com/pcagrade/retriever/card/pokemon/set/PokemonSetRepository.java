package com.pcagrade.retriever.card.pokemon.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PokemonSetRepository extends MasonRepository<PokemonSet, Ulid> {
    @Procedure("j_merge_set")
    void mergeSets(Ulid id1, Ulid id2);
    boolean existsByIdPca(int idPca);

    Optional<PokemonSet> findFirstByShortName(String shortName);


        @Query("SELECT ps FROM PokemonSet ps JOIN FETCH ps.cards WHERE ps.id = :id")
        Optional<PokemonSet> findByIdWithCards(@Param("id") Ulid id);
}
