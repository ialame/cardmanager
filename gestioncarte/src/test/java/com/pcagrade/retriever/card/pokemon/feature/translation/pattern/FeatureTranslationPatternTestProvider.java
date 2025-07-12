package com.pcagrade.retriever.card.pokemon.feature.translation.pattern;

import com.pcagrade.retriever.card.pokemon.feature.FeatureTestProvider;
import com.pcagrade.mason.localization.Localization;

public class FeatureTranslationPatternTestProvider {

    public static FeatureTranslationPattern megaPattern() {
        var feature = new FeatureTranslationPattern();

        feature.setTitle("Mega");
        feature.setImgHref("Mega");
        feature.setId(1);
        feature.setRegex("(^|[\\s-]*)Mega([\\s-]+|$)");
        feature.setSource("bulbapedia");
        feature.setLocalization(Localization.USA);
        feature.setFeature(FeatureTestProvider.mega());
        return feature;
    }

    public static FeatureTranslationPattern exPattern() {
        var feature = new FeatureTranslationPattern();

        feature.setTitle("EX");
        feature.setImgHref("EX");
        feature.setId(2);
        feature.setRegex("(^|[\\s-]*)EX([\\s-]+|$)");
        feature.setSource("bulbapedia");
        feature.setLocalization(Localization.USA);
        feature.setFeature(FeatureTestProvider.ex());
        return feature;
    }

    public static FeatureTranslationPattern exPatternWikiFr() {
        var feature = new FeatureTranslationPattern();

        feature.setTitle("-EX");
        feature.setImgHref("Pok%C3%A9mon-EX");
        feature.setId(3);
        feature.setRegex("(^|[\\s-]*)EX([\\s-]+|$)");
        feature.setSource("wiki-fr-pokepedia");
        feature.setLocalization(Localization.FRANCE);
        feature.setFeature(FeatureTestProvider.ex());
        return feature;
    }

    public static FeatureTranslationPattern exNonTeraPatternWikiFr() {
        var feature = new FeatureTranslationPattern();

        feature.setTitle("-ex");
        feature.setImgHref("Pok%C3%A9mon-ex");
        feature.setId(4);
        feature.setRegex("(^|[\\s-]*)ex([\\s-]+|$)");
        feature.setSource("wiki-fr-pokepedia");
        feature.setLocalization(Localization.FRANCE);
        feature.setFeature(FeatureTestProvider.exNonTera());
        return feature;
    }

    public static FeatureTranslationPatternDTO megaPatternDTO() {
        var feature = new FeatureTranslationPatternDTO();

        feature.setId(1);
        feature.setTitle("Mega");
        feature.setImgHref("Mega");
        feature.setRegex("(^|[\\s-]*)Mega([\\s-]+|$)");
        feature.setSource("bulbapedia");
        feature.setLocalization(Localization.USA);
        return feature;
    }
}
