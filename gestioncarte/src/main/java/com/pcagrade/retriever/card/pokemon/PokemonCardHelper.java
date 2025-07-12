package com.pcagrade.retriever.card.pokemon;

import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.PCAUtils;
import jakarta.annotation.Nonnull;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class PokemonCardHelper {

    public static final String STAFF = "staff";
    public static final String NO_NUMBER = "—";
    public static final List<String> NO_NUMBERS = List.of(NO_NUMBER, "None");
    public static final String FA_SUFFIX = " FA";
    private static final Pattern TRAINER_PATTERN = Pattern.compile("\\s?[（\\(].*[\\)）]");
    private static final Pattern ALTERNATE_PATTERN = Pattern.compile("(([a-z]/\\d*)|(\\d*[a-z]))$");

    private PokemonCardHelper() {}

    @Nonnull
    public static <T> Optional<T> findTranslation(String number, Map<String, T> map) {
        if (MapUtils.isNotEmpty(map)) {
            var translated = map.get(number);

            if (translated == null) {
                var split = number.split("/");

                if (split.length > 1) {
                    translated = map.get(rebuildNumber(split[0], split[1]));

                    if (translated == null) {
                        translated = map.get(rebuildNumber("%d/%s", split[0], split[1]));
                    }
                }
                if (translated == null) {
                    translated = map.get(rebuildNumber("%d%s", split[0], ""));
                }
                if (translated == null) {
                    translated = map.get(rebuildNumber("%03d%s", split[0], ""));
                }
                if (translated == null) {
                    translated = map.get(split[0]);
                }
            }
            return Optional.ofNullable(translated);
        }
        return Optional.empty();
    }

    public static String rebuildNumber(String number, String total) {
        return rebuildNumber("%d/%s", number, total);
        //return rebuildNumber("%03d/%s", number, total);
    }
    public static String rebuildNumber0(String number, String total) {
        return rebuildNumber("%03d/%s", number, total);
    }
    private static String rebuildNumber(String format, String number, String total) {
        try {
            return String.format(format, Integer.parseInt(number), total);
        } catch (NumberFormatException e) {
            return number + "/" + total;
        }
    }

    public static Pair<String, String> extractTrainer(String name) {
        if (StringUtils.isBlank(name)) {
            return Pair.of("", "");
        }

        var newName = name.replace("’", "'");
        var matcher = TRAINER_PATTERN.matcher(newName);

        if (matcher.find()) {
            var group = matcher.group();

            return Pair.of(PCAUtils.clean(matcher.replaceAll("")), getTrainer(group));
        }
        return Pair.of(PCAUtils.clean(newName), "");
    }

    private static String getTrainer(String group) {
        if (StringUtils.isBlank(group)) {
            return "";
        }
        var trainer = PCAUtils.substringBetween(group, "(", ")");

        if (StringUtils.isBlank(trainer)) {
            trainer = PCAUtils.substringBetween(group, "（", "）");
        }
        return PCAUtils.clean(trainer);
    }

    public static boolean isNoNumber(String number) {
        return NO_NUMBERS.contains(StringUtils.trimToEmpty(number));
    }

    public static boolean inUnnumbered(String number, String setTotalNumber) {
        return StringUtils.isBlank(number) || isNoNumber(number) || number.equalsIgnoreCase(setTotalNumber);
    }

    public static boolean isAlternate(String number) {
        if (StringUtils.isBlank(number)) {
            return false;
        }
        return ALTERNATE_PATTERN.matcher(number).find();
    }

    public static String removeAlternate(String number) {
        if (StringUtils.isBlank(number)) {
            return "";
        }
        return RegExUtils.removePattern(number, "[a-z]$");
    }

    public static String removeBoldBracket(PokemonCardDTO card, String name, Localization localization) {
        if (StringUtils.isBlank(name)) {
            return "";
        }

        for (var bracket : card.getBrackets()) {
            if (bracket.isBold()) {
                var translation = bracket.getTranslations().get(localization);

                if (translation != null) {
                    var bracketName = translation.getName();

                    name = PCAUtils.clean(StringUtils.removeEnd(name, bracketName));
                }
            }
        }
        return name;
    }

    public static Localization otherLocalization(Localization localization) {
        return localization == Localization.JAPAN ? Localization.USA : Localization.JAPAN;
    }

}
