package com.pcagrade.mason.commons;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class FilterHelper {

    private FilterHelper() { }

    public static <T> Predicate<T> distinctByComparator(Comparator<? super T> comparator) {
        List<T> seen = new ArrayList<>();

        return t1 -> {
            synchronized (seen) {
                for (var t2 : seen) {
                    if (comparator.compare(t1, t2) == 0) {
                        return false;
                    }
                }
                seen.add(t1);
                return true;
            }
        };
    }

    public static <T> Predicate<T> distinctFromPrevious() {
        var previous = new AtomicReference<T>();

        return t1 -> {
            synchronized (previous) {
                var v = previous.get();

                if (v != null && Objects.equals(t1, v)) {
                    return false;
                }
                previous.set(t1);
                return true;
            }
        };
    }

    public static <T> Predicate<T> distinctFromPreviousByComparator(Comparator<? super T> comparator) {
        var previous = new AtomicReference<T>();

        return t1 -> {
            synchronized (previous) {
                var v = previous.get();

                if (v != null && comparator.compare(t1, v) == 0) {
                    return false;
                }
                previous.set(t1);
                return true;
            }
        };
    }
}
