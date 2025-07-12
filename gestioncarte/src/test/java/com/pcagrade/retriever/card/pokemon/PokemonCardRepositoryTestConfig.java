package com.pcagrade.retriever.card.pokemon;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.TestUlidProvider;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.bracket.BracketTestProvider;
import com.pcagrade.retriever.card.pokemon.set.PokemonSet;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetTestProvider;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslation;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationRepository;
import com.pcagrade.retriever.card.promo.PromoCard;
import com.pcagrade.retriever.card.promo.event.PromoCardEventTestProvider;
import com.pcagrade.mason.localization.Localization;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@RetrieverTestConfiguration
public class PokemonCardRepositoryTestConfig {

    public static PokemonCardTranslation venusaur() {
        var us = new PokemonCardTranslation();
        var jp = new PokemonCardTranslation();
        var card = new PokemonCard();

        card.setId(TestUlidProvider.ID_101);
        card.getCardSets().add(PokemonSetTestProvider.xy());
        card.getCardSets().add(PokemonSetTestProvider.collectionX());
        us.setId(TestUlidProvider.ID_1);
        us.setLocalization(Localization.USA);
        us.setName("Venusaur");
        us.setNumber("2/146");
        card.setTranslation(Localization.USA, us);
        jp.setId(TestUlidProvider.ID_3);
        jp.setLocalization(Localization.JAPAN);
        jp.setName("Venusaur");
        jp.setNumber("002/060");
        card.setTranslation(Localization.JAPAN, jp);
        return us;
    }

    public static PokemonCardTranslation applin() {
        var us = new PokemonCardTranslation();
        var card = new PokemonCard();
        var set = new PokemonSet();

        set.setId(PokemonSetTestProvider.REBEL_CLASH_ID);
        card.setId(TestUlidProvider.ID_2);
        card.getCardSets().add(set);
        us.setId(TestUlidProvider.ID_2);
        us.setLocalization(Localization.USA);
        us.setName("Applin");
        us.setNumber("020/192");
        card.setTranslation(Localization.USA, us);
        card.setFullArt(true);
        return us;
    }

    private static PokemonCardTranslation frosmoth(Ulid id, String promo) {
        var us = new PokemonCardTranslation();
        var card = new PokemonCard();
        var set = new PokemonSet();

        set.setId(PokemonSetTestProvider.SWSH_ID);
        card.setId(id);
        card.getCardSets().add(set);
        us.setId(id);
        us.setLocalization(Localization.USA);
        us.setName("Frosmoth");
        us.setLabelName("Frosmoth");
        us.setNumber("SWSH007");
        card.setTranslation(Localization.USA, us);
        card.setFullArt(false);

        var promoCard = new PromoCard();
        var event = PromoCardEventTestProvider.testEvent();

        promoCard.setId(id);
        promoCard.setLocalization(Localization.USA);
        promoCard.setName(promo);
        promoCard.setEvent(event);
        promoCard.setCard(card);
        card.setPromoCards(new HashSet<>(Set.of(promoCard)));
        return us;
    }

    public static PokemonCard skiddoXY11() {
        var us = new PokemonCardTranslation();
        var card = new PokemonCard();

        card.setId(TestUlidProvider.ID_8);
        card.getCardSets().add(PokemonSetTestProvider.promoXY());
        us.setId(TestUlidProvider.ID_8);
        us.setLocalization(Localization.USA);
        us.setName("Skiddo");
        us.setLabelName("Skiddo");
        us.setNumber("XY11");
        card.setTranslation(Localization.USA, us);
        card.setFullArt(false);

        var promoCard = new PromoCard();

        promoCard.setId(TestUlidProvider.ID_8);
        promoCard.setLocalization(Localization.USA);
        promoCard.setName("Flashfire Blisters");
        promoCard.setCard(card);
        card.setPromoCards(Set.of(promoCard));
        return card;
    }


    public static PokemonCardTranslation frosmothParticipation() {
        return frosmoth(TestUlidProvider.ID_3, "Sword & Shield Prerelease participation promo");
    }

    public static PokemonCardTranslation frosmothStaff() {
        var us = frosmoth(TestUlidProvider.ID_4, "Sword & Shield Prerelease staff promo");
        var card = us.getCard();

        Objects.requireNonNull(card).setBrackets(Set.of(BracketTestProvider.staff()));
        return us;
    }


    public static PokemonCardTranslation blainesCharmander() {
        var jp = new PokemonCardTranslation();
        var card = new PokemonCard();

        card.setId(TestUlidProvider.ID_7);
        card.getCardSets().add(PokemonSetTestProvider.gurenTownGym());
        jp.setId(TestUlidProvider.ID_3);
        jp.setLocalization(Localization.JAPAN);
        jp.setName("Blaine's Charmander");
        jp.setNumber(PokemonCardHelper.NO_NUMBER);
        card.setTranslation(Localization.JAPAN, jp);
        return jp;
    }

    public static final List<PokemonCard> CARDS;

    static {
        var venusaurFA = venusaur().getCard();

        venusaurFA.setFullArt(true);
        venusaurFA.setId(TestUlidProvider.ID_102);

        CARDS = List.of(
                venusaur().getCard(),
                venusaurFA,
                applin().getCard(),
                frosmothParticipation().getCard(),
                frosmothStaff().getCard(),
                skiddoXY11(),
                blainesCharmander().getCard()
        );
    }

    @Bean
    public PokemonCardRepository pokemonCardRepository() {
        var repository = RetrieverTestUtils.mockRepository(PokemonCardRepository.class, CARDS, PokemonCard::getId);

        Mockito.when(repository.findInSet(Mockito.any(Ulid.class))).thenAnswer(invocation -> {
            var setId = invocation.getArgument(0, Ulid.class);

            return CARDS.stream()
                    .filter(card -> card.getCardSets().stream().anyMatch(set -> set.getId().equals(setId)))
                    .toList();
        });
        return repository;
    }

    @Bean
    public PokemonCardTranslationRepository pokemonCardTranslationRepository() {
        var list = List.of( // TODO extract this list
                venusaur(),
                applin(),
                frosmothParticipation(),
                frosmothStaff(),
                (PokemonCardTranslation) skiddoXY11().getTranslation(Localization.USA)
        );

        var repository = RetrieverTestUtils.mockRepository(PokemonCardTranslationRepository.class, list, PokemonCardTranslation::getId);

        Mockito.when(repository.findAllByNameAndNumberAndLocalizationAndSetId(Mockito.anyString(), Mockito.anyString(), Mockito.any(Localization.class), Mockito.any(Ulid.class))).thenAnswer(invocation -> {
            var name = invocation.getArgument(0, String.class);
            var number = invocation.getArgument(1, String.class);
            var localization = invocation.getArgument(2, Localization.class);
            var setId = invocation.getArgument(3, Ulid.class);

            return list.stream()
                    .filter(card -> card.getName().equals(name))
                    .filter(card -> card.getNumber().equals(number))
                    .filter(card -> card.getLocalization().equals(localization))
                    .filter(card -> card.getCard().getCardSets().stream().anyMatch(set -> set.getId().equals(setId)))
                    .toList();
        });
        Mockito.when(repository.findAllByNumberAndLocalizationAndSetId(Mockito.anyString(), Mockito.any(Localization.class), Mockito.any(Ulid.class))).thenAnswer(invocation -> {
            var number = invocation.getArgument(0, String.class);
            var localization = invocation.getArgument(1, Localization.class);
            var setId = invocation.getArgument(2, Ulid.class);

            return list.stream()
                    .filter(card -> card.getNumber().equals(number))
                    .filter(card -> card.getLocalization().equals(localization))
                    .filter(card -> card.getCard().getCardSets().stream().anyMatch(set -> set.getId().equals(setId)))
                    .toList();
        });
        return repository;
    }

}
