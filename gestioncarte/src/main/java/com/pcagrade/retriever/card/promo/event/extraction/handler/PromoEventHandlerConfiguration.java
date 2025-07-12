package com.pcagrade.retriever.card.promo.event.extraction.handler;

import com.pcagrade.retriever.card.pokemon.set.PokemonSetService;
import com.pcagrade.retriever.card.promo.PromoCardService;
import com.pcagrade.retriever.card.promo.event.PromoCardEventService;
import com.pcagrade.retriever.card.promo.event.extraction.handler.onepiece.AddOnePieceTranslationEventHandler;
import com.pcagrade.retriever.card.promo.event.extraction.handler.pokemon.AddPokemonTranslationEventHandler;
import com.pcagrade.retriever.card.promo.event.extraction.handler.pokemon.ChampionshipPromoEventHandler;
import com.pcagrade.retriever.card.promo.event.extraction.handler.pokemon.ExclusivePromoEventHandler;
import com.pcagrade.retriever.card.promo.event.extraction.handler.pokemon.HoloStampPromoEventHandler;
import com.pcagrade.retriever.card.promo.event.extraction.handler.pokemon.LanguageOnlyPromoEventHandler;
import com.pcagrade.retriever.card.promo.event.extraction.handler.pokemon.ParenthesisTraitPromoEventHandler;
import com.pcagrade.retriever.card.promo.event.extraction.handler.pokemon.SpecificPromoEventHandler;
import com.pcagrade.retriever.card.promo.event.extraction.handler.pokemon.StampTraitPromoEventHandler;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTrait;
import com.pcagrade.retriever.card.promo.event.trait.PromoCardEventTraitService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class PromoEventHandlerConfiguration {

    @Bean
    @Order(0)
    public CleanNamePromoEventHandler cleanNamePromoEventHandler() {
        return new CleanNamePromoEventHandler();
    }

    @Bean
    @Order(1)
    public AddPokemonTranslationEventHandler addPokemonTranslationEventHandler(PokemonSetService pokemonSetService) {
        return new AddPokemonTranslationEventHandler(pokemonSetService);
    }

    @Bean
    @Order(1)
    public AddOnePieceTranslationEventHandler addOnePieceTranslationEventHandler() {
        return new AddOnePieceTranslationEventHandler();
    }

    @Bean
    @Order(2)
    public LanguageOnlyPromoEventHandler languageOnlyPromoEventHandler() {
        return new LanguageOnlyPromoEventHandler();
    }

    @Bean
    @Order(3)
    public ParseDatePromoEventHandler parseDatePromoEventHandler() {
        return new ParseDatePromoEventHandler();
    }

    @Bean
    @Order(4)
    public ExistingEventHandler existingEventHandler(PromoCardService promoCardService, PromoCardEventService promoCardEventService) {
        return new ExistingEventHandler(promoCardService, promoCardEventService);
    }

    @Bean
    @Order(5)
    public ExistingTraitPromoEventHandler existingTraitPromoEventHandler(PromoCardEventTraitService promoCardEventTraitService) {
        return new ExistingTraitPromoEventHandler(promoCardEventTraitService);
    }

    @Bean
    @Order(6)
    public StampTraitPromoEventHandler stampTraitPromoEventHandler() {
        return new StampTraitPromoEventHandler();
    }

    @Bean
    @Order(7)
    public ParenthesisTraitPromoEventHandler parenthesisTraitPromoEventHandler() {
        return new ParenthesisTraitPromoEventHandler();
    }

    @Bean
    @Order(8)
    public FinalTraitPromoEventHandler finalTraitPromoEventHandler() {
        return new FinalTraitPromoEventHandler();
    }

    @Bean
    @Order(9)
    public ExclusivePromoEventHandler exclusiveHoloPromoEventHandler() {
        return new ExclusivePromoEventHandler(PromoCardEventTrait.HOLO);
    }

    @Bean
    @Order(10)
    public ExclusivePromoEventHandler exclusiveStampPromoEventHandler() {
        return new ExclusivePromoEventHandler(PromoCardEventTrait.STAMP);
    }

    @Bean
    @Order(11)
    public SpecificPromoEventHandler specificPromoEventHandler() {
        return new SpecificPromoEventHandler();
    }

    @Bean
    @Order(12)
    public HoloStampPromoEventHandler holoStampPromoEventHandler() {
        return new HoloStampPromoEventHandler();
    }

    @Bean
    @Order(13)
    public NameFromTraitsPromoEventHandler nameFromTraitsPromoEventHandler() {
        return new NameFromTraitsPromoEventHandler();
    }

    @Bean
    @Order(14)
    public ChampionshipPromoEventHandler championshipPromoEventHandler() {
        return new ChampionshipPromoEventHandler();
    }

    @Bean
    @Order(15)
    @Deprecated(forRemoval = true)
    public OldDatePromoEventHandler oldDatePromoEventHandler(PromoCardEventService promoCardEventService) {
        return new OldDatePromoEventHandler(promoCardEventService);
    }

    @Bean
    @Order(16)
    public SortTraitPromoEventHandler sortTraitPromoEventHandler() {
        return new SortTraitPromoEventHandler();
    }

    @Bean
    @Order(17)
    public FinalCleanPromoEventHandler finalCleanPromoEventHandler() {
        return new FinalCleanPromoEventHandler();
    }
}
