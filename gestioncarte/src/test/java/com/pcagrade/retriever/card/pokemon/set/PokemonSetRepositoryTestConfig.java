package com.pcagrade.retriever.card.pokemon.set;

import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.set.extraction.status.CardSetExtractionStatusRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

import java.util.List;

@RetrieverTestConfiguration
public class PokemonSetRepositoryTestConfig {

    @Bean
    public CardSetExtractionStatusRepository cardSetExtractionStatusRepository() {
        return Mockito.mock(CardSetExtractionStatusRepository.class);
    }

    @Bean
    public PokemonSetRepository pokemonSetRepository() {
        return RetrieverTestUtils.mockRepository(PokemonSetRepository.class, List.of(
                PokemonSetTestProvider.collectionX(),
                PokemonSetTestProvider.rebelClash(),
                PokemonSetTestProvider.astralRadiance(),
                PokemonSetTestProvider.xy(),
                PokemonSetTestProvider.latiasHalfDeck(),
                PokemonSetTestProvider.pokemonGOus(),
                PokemonSetTestProvider.pokemonGOjp(),
                PokemonSetTestProvider.lostThunder(),
                PokemonSetTestProvider.superBurstImpact(),
                PokemonSetTestProvider.alterGenesis(),
                PokemonSetTestProvider.swsh(),
                PokemonSetTestProvider.promoDP(),
                PokemonSetTestProvider.gurenTownGym(),
                PokemonSetTestProvider.promoXY(),
                PokemonSetTestProvider.promoSM(),
                PokemonSetTestProvider.promoSWSH(),
                PokemonSetTestProvider.smP(),
                PokemonSetTestProvider.sP(),
                PokemonSetTestProvider.brilliantStars(),
                PokemonSetTestProvider.vmaxClimax(),
                PokemonSetTestProvider.pikachusNewFriends(),
                PokemonSetTestProvider.platinum(),
                PokemonSetTestProvider.galacticsConquest(),
                PokemonSetTestProvider.vunionSpecialCardSets(),
                PokemonSetTestProvider.incandescentArcana(),
                PokemonSetTestProvider.legendsAwakened(),
                PokemonSetTestProvider.templeOfAnger(),
                PokemonSetTestProvider.deoxys(),
                PokemonSetTestProvider.pcgP(),
                PokemonSetTestProvider.zacianAndZamazentaBox(),
                PokemonSetTestProvider.scarletEx(),
                PokemonSetTestProvider.masterKitSideDeck(),
                PokemonSetTestProvider.emerald(),
                PokemonSetTestProvider.quaxlyAndMimikyuEX(),
                PokemonSetTestProvider.scarletAndViolet(),
                PokemonSetTestProvider.prizePackSeries1()
        ), PokemonSet::getId);
    }

}
