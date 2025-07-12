package com.pcagrade.retriever.card.yugioh;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Nonnull;
import java.util.regex.Pattern;

public class YuGiOhCardHelper {

    private static final Pattern NUMBER_PREFIX_PATTERN = Pattern.compile("^[A-Z|0-9]+-(?:SP|[A-Z]*?(?=SP?|\\d))");

    private YuGiOhCardHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    @Nonnull
    public static String extractCardNumber(String cardNumber) {
        return StringUtils.trimToEmpty(RegExUtils.removeAll(cardNumber, NUMBER_PREFIX_PATTERN));
    }

}
