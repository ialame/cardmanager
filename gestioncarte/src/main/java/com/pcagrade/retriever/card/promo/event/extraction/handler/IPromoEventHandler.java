package com.pcagrade.retriever.card.promo.event.extraction.handler;

import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Nonnull;
import java.util.regex.Pattern;

public interface IPromoEventHandler {

    @Nonnull
    static String createWordExtractRegex(@Nonnull String text) {
        return "(^|\\s)\\Q" + text + "\\E(\\s|$)";
    }

    default boolean supports(@Nonnull ExtractedPromoCardEventDTO dto) {
        return true;
    }

    void handleEvent(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull EventNameHolder nameToParse);

    class EventNameHolder implements CharSequence, Appendable {

        private final StringBuilder builder;

        public EventNameHolder(String name) {
            this.builder = new StringBuilder(name);
        }

        @Override
        public int length() {
            return builder.length();
        }

        @Override
        public char charAt(int index) {
            return builder.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return builder.subSequence(start, end);
        }

        @Override
        public EventNameHolder append(CharSequence csq) {
            builder.append(csq);
            return this;
        }

        @Override
        public EventNameHolder append(CharSequence csq, int start, int end) {
            builder.append(csq, start, end);
            return this;
        }

        @Override
        public EventNameHolder append(char c) {
            builder.append(c);
            return this;
        }

        public void set(String name) {
            builder.setLength(0);
            builder.append(name);
        }

        public boolean removeWord(String word) {
            return replaceAll(createWordExtractRegex(word), " ");
        }

        public boolean removeWordIgnoreCase(String word) {
            return replaceAll(Pattern.compile(createWordExtractRegex(word), Pattern.CASE_INSENSITIVE), " ");
        }

        public boolean remove(Pattern regex) {
            return replaceAll(regex, "");
        }

        public boolean replaceAll(String regex, String replacement) {
            return replaceAll(Pattern.compile(regex), replacement);
        }

        public boolean replaceAll(Pattern regex, String replacement) {
            var old = builder.toString();

            set(PCAUtils.clean(RegExUtils.replaceAll(builder.toString(), regex, replacement)));
            return !StringUtils.equals(old, builder);
        }

        @Override
        public String toString() {
            return builder.toString();
        }
    }
}
