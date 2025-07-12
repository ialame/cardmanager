package com.pcagrade.retriever.card.pokemon.translation;

import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.localization.translation.ITranslator;

import java.util.Optional;

public class UsToJapanTranslatorWrapper implements ITranslator<PokemonCardTranslationDTO>, ITranslationSourceUrlProvider {

    private final ITranslator<PokemonCardTranslationDTO> translator;
    private final PokemonCardTranslationDTO usSource;

    public UsToJapanTranslatorWrapper(ITranslator<PokemonCardTranslationDTO> translator, PokemonCardTranslationDTO usSource) {
        this.translator = translator;
        this.usSource = usSource;
    }

    @Override
    public Optional<PokemonCardTranslationDTO> translate(PokemonCardTranslationDTO source, Localization localization) {
        if (localization == Localization.JAPAN) {
            var opt = translator.translate(usSource, Localization.USA);

            opt.ifPresent(t -> {
                t.setLocalization(Localization.JAPAN);
                t.setNumber(source.getNumber());
                t.setRarity(source.getRarity());
            });
            return opt;
        }
        return Optional.empty();
    }

    @Override
    public String getName() {
        return translator.getName() + "-to-jp";
    }

    @Override
    public String getUrl(Localization localization) {
        if (translator instanceof ITranslationSourceUrlProvider provider) {
            return provider.getUrl(localization == Localization.JAPAN ? Localization.USA : localization);
        }
        return "";
    }
}
