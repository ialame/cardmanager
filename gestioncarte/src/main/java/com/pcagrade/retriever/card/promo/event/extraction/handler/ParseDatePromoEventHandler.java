package com.pcagrade.retriever.card.promo.event.extraction.handler;

import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.promo.event.extraction.ExtractedPromoCardEventDTO;
import com.pcagrade.retriever.date.DateHelper;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.regex.Pattern;

public class ParseDatePromoEventHandler implements IPromoEventHandler {

    private static final List<Pattern> PATTERNS = List.of(
            Pattern.compile("\\((.*)\\)"),
            Pattern.compile("in ([A-z]+\\s[0-9]{4})", Pattern.CASE_INSENSITIVE),
            Pattern.compile("for ([A-z]+\\s[0-9]{4})", Pattern.CASE_INSENSITIVE)
    );

    @Override
    public void handleEvent(@Nonnull ExtractedPromoCardEventDTO dto, @Nonnull EventNameHolder nameToParse) {
        for (var pattern : PATTERNS) {
            var matcher = pattern.matcher(nameToParse.toString());

            if (matcher.find()) {
                var matched = matcher.group(0);
                var dateStr = matcher.group(1);
                var date = DateHelper.parseDate(dateStr);

                if (date != null) {
                    var event = dto.getEvent();

                    event.getTranslations().forEach((l, t) -> {
                        t.setName(removeDate(t.getName(), matched));
                        t.setLabelName(removeDate(t.getLabelName(), matched));
                        t.setReleaseDate(date);
                    });
                    nameToParse.set(removeDate(nameToParse.toString(), matched));
                    return;
                }
            }
        }
    }

    @Nonnull
    private static String removeDate(String name, String substring) {
        return PCAUtils.clean(StringUtils.removeIgnoreCase(name, substring));
    }
}
