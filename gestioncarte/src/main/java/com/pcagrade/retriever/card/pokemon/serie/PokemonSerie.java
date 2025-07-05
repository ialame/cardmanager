package com.pcagrade.retriever.card.pokemon.serie;

import com.pcagrade.retriever.serie.Serie;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "pokemon_serie")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("pok")
public class PokemonSerie extends Serie {

    @Column(name = "id_pca")
    private Integer idPca;

    public Integer getIdPca() {
        return idPca;
    }

    public void setIdPca(Integer idPca) {
        this.idPca = idPca;
    }
}
