package com.pcagrade.retriever;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
class PCAUtilsShould {

	@Test
	void splitValidList_on_0() {
		List<List<Integer>> list = PCAUtils.split(List.of(1, 2, 3, 0, 4, 5, 6, 0, 7, 8), i -> i.equals(0)).toList();

		assertThat(list)
				.hasSize(3)
				.allMatch(l -> !l.contains(0));
	}

	@Test
	void splitAndKeepValidList_on_0() {
		List<List<Integer>> list = PCAUtils.splitAndKeep(List.of(1, 2, 3, 0, 4, 5, 6, 0, 7, 8), i -> i.equals(0)).toList();

		assertThat(list)
				.hasSize(3);
	}

	@Test
	void replaceAllWithSpace() {
		assertThat(PCAUtils.replaceAllWithSpace(Pattern.compile(" b "), "a b c", "d")).isEqualTo("a d c");
		assertThat(PCAUtils.replaceAllWithSpace(Pattern.compile("(^|[\\s-]*)EX([\\s-]+|$)"), "Venusaur EX", "EX test")).isEqualTo("Venusaur EX test");
	}

	@ParameterizedTest
	@MethodSource("provideInvalidArgs")
	void emptySubstring_when_nullArg(String text, String first, String second) {
		assertThat(PCAUtils.substringBetween(text, first, second)).isEmpty();
	}

	@ParameterizedTest
	@MethodSource("provideInvalidArgs")
	void emptySubstrings_when_nullArg(String text, String first, String second) {
		assertThat(PCAUtils.substringsBetween(text, first, second)).isEmpty();
	}

	@ParameterizedTest
	@MethodSource("provideEqualsLists")
	void collectionComparator(List<String> l1, List<String> l2) {
		assertThat(PCAUtils.collectionComparator(PCAUtils::compareTrimmedIgnoreCase).compare(l1, l2)).isZero();
	}

	@ParameterizedTest
	@MethodSource("provideNotEqualsLists")
	void collectionComparator_not_equals(List<String> l1, List<String> l2) {
		assertThat(PCAUtils.collectionComparator(PCAUtils::compareTrimmedIgnoreCase).compare(l1, l2)).isNotZero();
	}

	@ParameterizedTest
	@MethodSource("provideListWithMatch")
	void getFirstIndexMatching(List<String> list, int indexToMatch) {
		assertThat(PCAUtils.getFirstIndexMatching(list, s -> s.contains("match"))).isEqualTo(indexToMatch);
	}

	@ParameterizedTest
	@MethodSource("provideNamesToClean")
	void clean(String raw, String name) {
		var result = PCAUtils.clean(raw);

		assertThat(result).isEqualTo(name);
	}

	private static Stream<Arguments> provideInvalidArgs() {
		return Stream.of(
				Arguments.of("", "", ""),
				Arguments.of(null, null, null),
				Arguments.of("abc", "a", ""),
				Arguments.of("abc", null, ""),
				Arguments.of("abc", "a", null),
				Arguments.of("abcdef", "a", "a")
			);
	}

	private static Stream<Arguments> provideEqualsLists() {
		return Stream.of(
				Arguments.of(List.of(), List.of()),
				Arguments.of(null, null),
				Arguments.of(List.of(), null),
				Arguments.of(List.of(""), List.of("")),
				Arguments.of(List.of("a"), List.of("a"))
		);
	}

	private static Stream<Arguments> provideNotEqualsLists() {
		return Stream.of(
				Arguments.of(List.of("a"), List.of()),
				Arguments.of(List.of("a"), null),
				Arguments.of(List.of(), List.of("a")),
				Arguments.of(null, List.of("a")),
				Arguments.of(List.of("a", "b"), List.of("a")),
				Arguments.of(List.of("a", "b"), List.of("a", "c"))
		);
	}

	private static Stream<Arguments> provideListWithMatch() {
		return Stream.of(
				Arguments.of(List.of(), -1),
				Arguments.of(null, -1),
				Arguments.of(List.of("a", "b", "c"), -1),
				Arguments.of(List.of("match", "b", "c"), 0),
				Arguments.of(List.of("a", "match", "c"), 1),
				Arguments.of(List.of("a", "b", "match"), 2),
				Arguments.of(List.of("a", "b", "c", "match"), 3),
				Arguments.of(List.of("a", "b", "c", "match", "d"), 3)
		);
	}

	private static Stream<Arguments> provideNamesToClean() {
		return Stream.of(
				Arguments.of("", ""),
				Arguments.of(null, ""),
				Arguments.of("test", "test"),
				Arguments.of(" test ", "test"),
				Arguments.of(" test", "test"),
				Arguments.of("test ", "test"),
				Arguments.of("test ", "test"),
				Arguments.of("foo bar", "foo bar"),
				Arguments.of("foo bar\u200B", "foo bar"),
				Arguments.of("foo bar\u00a0", "foo bar"),
				Arguments.of("foo bar\u3000", "foo bar"),
				Arguments.of("foo\u200Bbar", "foo bar"),
				Arguments.of("foo\u00a0bar", "foo bar"),
				Arguments.of("foo\u3000bar", "foo bar"),
				Arguments.of("foo  bar", "foo bar"),
				Arguments.of("  foo      bar ", "foo bar"),
				Arguments.of("test’s", "test's")
		);
	}

}
