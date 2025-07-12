package com.pcagrade.retriever.card.pokemon.translation.translator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "j_pokemon_card_translator")
public class PokemonCardTranslator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Column(name = "type", nullable = false, length = 10)
    private String type;

    @Column(name = "blocking", nullable = false)
    private boolean blocking = false;

    @Column(name = "url", nullable = true)
    private String url;

    @Lob
    @Column(name = "locales", columnDefinition = "LONGTEXT")
    private String localizations;

    @Lob
    @Column(name = "pattern_locales", columnDefinition = "LONGTEXT")
    private String patternLocalizations;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocalizations() {
        return localizations;
    }

    public void setLocalizations(String locales) {
        this.localizations = locales;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPatternLocalizations() {
        return patternLocalizations;
    }

    public void setPatternLocalizations(String patternLocalizations) {
        this.patternLocalizations = patternLocalizations;
    }
}
