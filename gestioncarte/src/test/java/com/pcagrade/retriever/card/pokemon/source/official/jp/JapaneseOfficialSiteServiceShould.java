package com.pcagrade.retriever.card.pokemon.source.official.jp;

import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.PokemonCardTestProvider;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import com.pcagrade.retriever.card.pokemon.translation.ITranslationSourceUrlProvider;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(JapaneseOfficialSiteTestConfig.class)
class JapaneseOfficialSiteServiceShould {

    @Autowired
    private JapaneseOfficialSiteService japaneseOfficialSiteService;

    @Test
    void translate_with_trainer() {
        var card = PokemonCardTestProvider.professorResearch();
        var translator = japaneseOfficialSiteService.createTranslator(PokemonSetTestProvider.lucarioVstarStarterSet(), card);
        var translation = translator.translate(card.getTranslations().get(Localization.JAPAN), Localization.JAPAN);

        assertThat(translation)
                .isNotEmpty()
                .hasValueSatisfying(t -> assertThat(t.getOriginalName()).isEqualTo("博士の研究"))
                .hasValueSatisfying(t -> assertThat(t.getTrainer()).isEqualTo("ナナカマド博士"));
    }

    @Test
    void getCardArtist() {
        var cardArtist = japaneseOfficialSiteService.getCardArtist(PokemonSetTestProvider.pokemonGOjpDTO(), PokemonCardTestProvider.bulbasaurDTO());

        assertThat(cardArtist).isEqualTo("sowsow");
    }

    @Test
    void getCardArtist_with_us_card() {
        var cardArtist = japaneseOfficialSiteService.getCardArtist(PokemonSetTestProvider.pokemonGOjpDTO(), PokemonCardTestProvider.venusaur());

        assertThat(cardArtist).isEmpty();
    }


    @Test
    void getUrl() {
        var urlProvider = getUrlProvider();
        var url = urlProvider.getUrl(Localization.JAPAN);

        assertThat(url).isEqualTo("https://www.pokemon-card.com/card-search/details.php/card/41682/regu/all");
    }

    @Test
    void getUrl_with_usa() {
        var urlProvider = getUrlProvider();
        var url = urlProvider.getUrl(Localization.USA);

        assertThat(url).isEmpty();
    }

    @Test
    void getUrl_with_usaCard() {
        var urlProvider = getUrlProvider(PokemonCardTestProvider.venusaur());
        var url = urlProvider.getUrl(Localization.JAPAN);

        assertThat(url).isEmpty();
    }

    @Test
    void getUrl_with_usaSet() {
        var urlProvider = getUrlProvider(PokemonSetTestProvider.pokemonGOusDTO(), PokemonCardTestProvider.bulbasaurDTO());
        var url = urlProvider.getUrl(Localization.JAPAN);

        assertThat(url).isEmpty();
    }

    private ITranslationSourceUrlProvider getUrlProvider() {
        return getUrlProvider(PokemonCardTestProvider.bulbasaurDTO());
    }

    private ITranslationSourceUrlProvider getUrlProvider(PokemonCardDTO card) {
        return getUrlProvider(PokemonSetTestProvider.pokemonGOjpDTO(), card);
    }

    private ITranslationSourceUrlProvider getUrlProvider(PokemonSetDTO set, PokemonCardDTO card) {
        return (ITranslationSourceUrlProvider) japaneseOfficialSiteService.createTranslator(set, card);
    }
}
