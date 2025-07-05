package com.pcagrade.retriever.date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DateHelper {

    private static final Logger LOGGER = LogManager.getLogger(DateHelper.class);
    private static final List<DateTimeFormatter> FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("[d][ ][MMMM][ ][d][,][ ]yyyy", Locale.US),
            DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US),
            DateTimeFormatter.ofPattern("yyyy", Locale.US)
    );

    private DateHelper() { }

    @Nullable
    public static LocalDate parseDate(String text) {
        var trimed = StringUtils.trimToEmpty(text);

        if (StringUtils.isBlank(trimed)) {
            return null;
        }

        return FORMATTERS.stream()
                .map(f -> {
                    try {
                        return f.parse(trimed, a -> {
                            var year = a.get(ChronoField.YEAR);
                            var month = safeGetField(a, ChronoField.MONTH_OF_YEAR);
                            var day = safeGetField(a, ChronoField.DAY_OF_MONTH);

                            return LocalDate.of(year, month, day);
                        });
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private static int safeGetField(TemporalAccessor accessor, ChronoField field) {
        try {
            return accessor.get(field);
        } catch (Exception e) {
            return 1;
        }
    }
}
