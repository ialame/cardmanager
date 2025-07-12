package com.pcagrade.retriever.card.pokemon.feature.translation.pattern;

import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FeatureTranslationPatternDTO implements ILocalized {

    private static final Logger LOGGER = LogManager.getLogger(FeatureTranslationPatternDTO.class);

    private int id;
    private Localization localization;
    private String source;
    private String imgHref;
    private String title;
    private String regex;
    private String replacementName;
    private String replacementLabelName;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Localization getLocalization() {
        return localization;
    }

    @Override
    public void setLocalization(Localization localization) {
        this.localization = localization;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getImgHref() {
        return imgHref;
    }

    public void setImgHref(String imgHref) {
        this.imgHref = imgHref;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getReplacementName() {
        return replacementName;
    }

    public void setReplacementName(String replacementName) {
        this.replacementName = replacementName;
    }

    public String getReplacementLabelName() {
        return replacementLabelName;
    }

    public void setReplacementLabelName(String replacementLabelName) {
        this.replacementLabelName = replacementLabelName;
    }

    public void matches(String name, BiConsumer<Pattern, Matcher> consumer) {
        this.<Void>matchesOrElse(name, (r, m) -> {
            consumer.accept(r, m);
            return null;
        }, () -> null);
    }

    public <T> T matchesOrElse(String name, BiFunction<Pattern, Matcher, ? extends T> function, Supplier<? extends T> supplier) {
        if (StringUtils.isNoneBlank(regex, name)) {
            try {
                var r = Pattern.compile(regex);
                Matcher match = r.matcher(name);

                if (match.find()) {
                    return function.apply(r, match);
                }
            } catch (PatternSyntaxException e) {
                LOGGER.error("Invalid pattern: {}\r\n{}", this::getRegex, e::getMessage);
            }
        }
        return supplier.get();
    }

    public boolean matches(String name) {
        return matchesOrElse(name, (r, m) -> true, () -> false);
    }
}
