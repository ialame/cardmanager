package com.pcagrade.retriever.card.pokemon.source.official.jp;

import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.translation.IPokemonCardTranslatorFactory;
import com.pcagrade.retriever.card.pokemon.translation.ITranslationSourceUrlProvider;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.ITranslator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JapaneseOfficialSiteService implements IPokemonCardTranslatorFactory {

    @Autowired
    private JapaneseOfficialSiteParser japaneseOfficialSiteParser;

    @Override
    public ITranslator<PokemonCardTranslationDTO> createTranslator(PokemonSetDTO set, PokemonCardDTO card) {
        return new JapaneseOfficialSiteTranslator(set, card);
    }

    public String getCardArtist(PokemonSetDTO set, PokemonCardDTO card) {
        var jp = card.getTranslations().get(Localization.JAPAN);

        if (jp == null) {
            return "";
        }
        return getValue(set, jp).artist();
    }

    private JapaneseOfficialSiteCard getValue(PokemonSetDTO set, PokemonCardTranslationDTO translation) {
        return japaneseOfficialSiteParser.parse(set.getId()).getOrDefault(translation.getNumber(), JapaneseOfficialSiteCard.DEFAULT);
    }

    private class JapaneseOfficialSiteTranslator implements ITranslator<PokemonCardTranslationDTO>, ITranslationSourceUrlProvider {

        private final PokemonSetDTO set;
        private final PokemonCardDTO card;

        private JapaneseOfficialSiteTranslator(PokemonSetDTO set, PokemonCardDTO card) {
            this.set = set;
            this.card = card;
        }

        @Override
        public String getUrl(Localization localization) {
            if (localization != Localization.JAPAN) {
                return "";
            }

            var translation = card.getTranslations().get(Localization.JAPAN);

            if (translation == null) {
                return "";
            }

            var url = getValue(translation).url();

            if (StringUtils.isBlank(url)) {
                return "";
            }

            return url;
        }

        private JapaneseOfficialSiteCard getValue(PokemonCardTranslationDTO translation) {
            return JapaneseOfficialSiteService.this.getValue(set, translation);
        }

        @Override
        public Optional<PokemonCardTranslationDTO> translate(PokemonCardTranslationDTO source, Localization localization) {
            if (localization != Localization.JAPAN) {
                return Optional.empty();
            }

            var value = getValue(source);

            var name = value.name();

            if (StringUtils.isBlank(name)) {
                return Optional.empty();
            }

            var copy = source.copy();

            copy.setName("");
            copy.setLabelName("");
            copy.setLocalization(Localization.JAPAN);
            copy.setOriginalName(name);
            copy.setTrainer(value.trainer());
            return Optional.of(copy);
        }

        @Override
        public String getName() {
            return "official-site-jp";
        }
    }
}
