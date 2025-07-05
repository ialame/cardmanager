package com.pcagrade.retriever.card.pokemon.source.pkmncards;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.PokemonCardHelper;
import com.pcagrade.retriever.card.pokemon.PokemonFieldMappingService;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.translation.IPokemonCardTranslatorFactory;
import com.pcagrade.retriever.card.pokemon.translation.ITranslationSourceUrlProvider;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.ITranslator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class PkmncardsComService implements IPokemonCardTranslatorFactory {

    @Autowired
    private PkmncardsComParser pkmncardsComParser;
    @Autowired
    private PokemonFieldMappingService fieldMappingService;

    public String getCardArtist(Ulid setId, PokemonCardDTO card) {
        return pkmncardsComParser.getCardArtist(setId, card);
    }

    public String getCardArtist(PokemonSetDTO set, PokemonCardDTO card) {
        return pkmncardsComParser.getCardArtist(set, card);
    }

    @Override
    public ITranslator<PokemonCardTranslationDTO> createTranslator(PokemonSetDTO set, PokemonCardDTO card) {
        return new PkmncardsComTranslator(set, card);
    }

    private class PkmncardsComTranslator implements ITranslator<PokemonCardTranslationDTO>, ITranslationSourceUrlProvider {

        private final PokemonSetDTO set;
        private final PokemonCardDTO card;

        private PkmncardsComTranslator(PokemonSetDTO set, PokemonCardDTO card) {
            this.set = set;
            this.card = card;
        }

        @Override
        public Optional<PokemonCardTranslationDTO> translate(PokemonCardTranslationDTO source, Localization localization) {
            if (localization == Localization.USA) {
                var pair = PokemonCardHelper.extractTrainer(pkmncardsComParser.getName(set, card));
                var name = PokemonCardHelper.removeBoldBracket(card, pair.getLeft(), localization);

                if (StringUtils.isNotBlank(name)) {
                    return Optional.of(createTranslation(source, name, pair.getRight(), localization));
                }
            }
            return Optional.empty();
        }

        private PokemonCardTranslationDTO createTranslation(PokemonCardTranslationDTO source, String cardName, String trainer, Localization localization) {
            var target = source.copy();

            target.setLocalization(localization);
            target.setName(cardName);
            target.setLabelName(cardName);
            target.setTrainer(trainer);
            target.setRarity(fieldMappingService.mapRarity(localization, pkmncardsComParser.getRarity(set, card)));
            return target;
        }

        @Override
        public String getName() {
            return "pkmncards-com";
        }

        @Override
        public String getUrl(Localization localization) {
            try {
                return pkmncardsComParser.getCardLink(set.getId(), card);
            } catch (IOException e) {
                return "";
            }
        }
    }

}
