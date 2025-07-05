package com.pcagrade.mason.commons;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FilterHelperTest {

    @Test
    void distinctByComparator() {
        var list = Stream.of("abc", "def", "aef")
                .filter(FilterHelper.distinctByComparator(Comparator.comparingInt(s -> StringUtils.isBlank(s) ? -1 : s.charAt(0))))
                .toList();

        assertThat(list).hasSize(2);
    }

    @Test
    void distinctFromPrevious() {
        var list = Stream.of("abc", "def", "def", "aef", "def")
                .filter(FilterHelper.distinctFromPrevious())
                .toList();

        assertThat(list).hasSize(4);
    }

    @Test
    void distinctFromPreviousByComparator() {
        var list = Stream.of("abc", "def", "def", "aef", "def")
                .filter(FilterHelper.distinctFromPreviousByComparator((s1, s2) -> StringUtils.compareIgnoreCase(StringUtils.trimToEmpty(s1), StringUtils.trimToEmpty(s2))))
                .toList();

        assertThat(list).hasSize(4);
    }
}
