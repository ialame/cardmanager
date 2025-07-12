package com.pcagrade.retriever.card.pokemon.source.pokecardex;

import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.PokemonCardHelper;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.source.pokecardex.code.PokecardexSetCodeService;
import com.pcagrade.retriever.card.pokemon.translation.IPokemonCardTranslatorFactory;
import com.pcagrade.retriever.card.pokemon.translation.ITranslationSourceUrlProvider;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.ITranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PokecardexComService implements IPokemonCardTranslatorFactory {

    @Autowired
    private PokecardexComParser pokecardexComParser;

    @Autowired
    private PokecardexSetCodeService pokecardexSetCodeService;

    @Override
    public ITranslator<PokemonCardTranslationDTO> createTranslator(PokemonSetDTO set, PokemonCardDTO card) {
        return new PokecardexComTranslator(pokecardexSetCodeService.getCode(set.getId()), card);
    }

    private class PokecardexComTranslator implements ITranslator<PokemonCardTranslationDTO>, ITranslationSourceUrlProvider {

        private final String code;
        private final PokemonCardDTO card;

        private PokecardexComTranslator(String code, PokemonCardDTO card) {
            this.code = code;
            this.card = card;
        }

        @Override
        public Optional<PokemonCardTranslationDTO> translate(PokemonCardTranslationDTO source, Localization localization) {
            if (localization == Localization.FRANCE && source != null) {
                return PokemonCardHelper.findTranslation(source.getNumber(), pokecardexComParser.parse(code)).map(n -> createTranslation(source, PokemonCardHelper.removeBoldBracket(card, n, localization)));
            }
            return Optional.empty();
        }

        private PokemonCardTranslationDTO createTranslation(PokemonCardTranslationDTO source, String name) {
            var target = source.copy();

            target.setLocalization(Localization.FRANCE);
            target.setName(name);
            target.setLabelName(name);
            target.setTrainer("");
            return target;
        }

        @Override
        public String getUrl(Localization localization) {
            return pokecardexComParser.getUrl(code);
        }

        @Override
        public String getName() {
            return "pokecardex-com";
        }
    }
}
