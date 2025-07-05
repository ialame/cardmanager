package com.pcagrade.retriever.card.pokemon.source.wiki;

import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.PokemonCardHelper;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.translation.IPokemonCardTranslatorFactory;
import com.pcagrade.retriever.card.pokemon.translation.ITranslationSourceUrlProvider;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.ITranslator;

import java.util.List;
import java.util.Optional;

public class PokemonWikiTranslatorFactory implements IPokemonCardTranslatorFactory {

    private final IPokemonWikiParser parser;

    protected PokemonWikiTranslatorFactory(IPokemonWikiParser parser) {
        this.parser = parser;
    }

    public IPokemonWikiParser getParser() {
        return parser;
    }

    @Override
    public ITranslator<PokemonCardTranslationDTO> createTranslator(PokemonSetDTO set, PokemonCardDTO card) {
        return new WikiTranslator(set, card);
    }

    public List<WikiFeaturePattern> searchPatterns(PokemonSetDTO set) {
        return parser.searchPatterns(set).stream()
                .map(p -> new WikiFeaturePattern(p.getLeft(), p.getRight()))
                .toList();
    }

    @Override
    public String toString() {
        return "wiki translator: " + parser.getName();
    }

    private class WikiTranslator implements ITranslator<PokemonCardTranslationDTO>, ITranslationSourceUrlProvider {

        private final PokemonSetDTO set;
        private final PokemonCardDTO card;

        private WikiTranslator(PokemonSetDTO set, PokemonCardDTO card) {
            this.set = set;
            this.card = card;
        }

        @Override
        public Optional<PokemonCardTranslationDTO> translate(PokemonCardTranslationDTO source, Localization localization) {
            if (source == null || !parser.canTranslateTo(localization)) {
                return Optional.empty();
            }

            return PokemonCardHelper.findTranslation(source.getNumber(), parser.parse(set)).map(p -> createTranslation(source, p.getLeft(), p.getRight(), localization));
        }

        private PokemonCardTranslationDTO createTranslation(PokemonCardTranslationDTO source, String name, String labelName, Localization localization) {
            var target = source.copy();
            var pair = PokemonCardHelper.extractTrainer(name);
            var labelPair = PokemonCardHelper.extractTrainer(labelName);

            target.setLocalization(localization);
            target.setName(PokemonCardHelper.removeBoldBracket(card, pair.getLeft(), localization));
            target.setLabelName(PokemonCardHelper.removeBoldBracket(card, labelPair.getLeft(), localization));
            target.setTrainer(PokemonCardHelper.removeBoldBracket(card, pair.getRight(), localization));
            return target;
        }

        @Override
        public String getUrl(Localization localization) {
            return parser.getUrl(set);
        }

        @Override
        public String getName() {
            return parser.getName();
        }
    }

}
