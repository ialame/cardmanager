package com.pcagrade.mason.localization;

import java.util.Comparator;
import java.util.Map;

public class LocalizationUtils {

    private LocalizationUtils() {}

    public static <T> Comparator<Map<Localization, T>> translationComparator(Comparator<T> comparator) {
        return (t1, t2) -> {
            int value = 0;

            for (var localization : Localization.values()) {
                var cmp1 = t1.containsKey(localization);
                var cmp2 = t2.containsKey(localization);

                if (cmp1 && cmp2) {
                    value = comparator.compare(t1.get(localization), t2.get(localization));

                    if (value != 0) {
                        return value;
                    }
                } else if (cmp1) {
                    return 1;
                } else if (cmp2) {
                    return -1;
                }
            }
            return value;
        };
    }

}
