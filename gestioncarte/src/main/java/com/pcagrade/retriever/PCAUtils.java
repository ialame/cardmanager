package com.pcagrade.retriever;

import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.ParameterizedTypeReference;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PCAUtils {

	private PCAUtils() {}

	public static <T> Stream<List<T>> split(List<T> list, Predicate<T> delimiter) {
		int[] indexes = Stream.of(IntStream.of(-1), IntStream.range(0, list.size()).filter(i -> delimiter.test(list.get(i))), IntStream.of(list.size())).flatMapToInt(s -> s).toArray();

		return IntStream.range(0, indexes.length - 1).mapToObj(i -> list.subList(indexes[i] + 1, indexes[i + 1]));
	}

	public static <T> Stream<List<T>> splitAndKeep(List<T> list, Predicate<T> delimiter) {
		int[] indexes = Stream.of(IntStream.of(-1), IntStream.range(0, list.size()).filter(i -> delimiter.test(list.get(i))), IntStream.of(list.size() - 1)).flatMapToInt(s -> s).toArray();

		return IntStream.range(0, indexes.length - 1).mapToObj(i -> list.subList(indexes[i] + 1, indexes[i + 1] + 1));
	}

	public static String substringAfter(String text, String first) {
		return StringUtils.trimToEmpty(StringUtils.substringAfter(text, first));
	}

	public static String substringBetween(String text, String first, String second) {
		return StringUtils.trimToEmpty(StringUtils.substringBetween(text, first, second));
	}

	public static List<String> substringsBetween(String text, String first, String second) {
		var substrings = StringUtils.substringsBetween(text, first, second);

		if (substrings == null) {
			return Collections.emptyList();
		}
		return Arrays.stream(substrings)
				.map(StringUtils::trimToEmpty)
				.filter(StringUtils::isNotBlank)
				.toList();
	}

	public static <T, U> void mergeCollections(Function<T, Collection<U>> getter, BiConsumer<U, T> setter, T source, T target) {
		Collection<U> sourceCollection = getter.apply(source);
		Collection<U> targetCollection = getter.apply(target);

		sourceCollection.forEach(entry -> {
			setter.accept(entry, target);
			targetCollection.add(entry);
		});
		sourceCollection.clear();
	}

	public static <T, U> void mergeCollections(Function<T, Collection<U>> getter, Function<U, Collection<T>> getter2, T source, T target) {
		mergeCollections(getter, (event, t) -> {
			var c = getter2.apply(event);

			c.remove(source);
			c.add(target);
		}, source, target);
	}

	public static <T> Comparator<Collection<T>> collectionComparator(Comparator<? super T> source) {
		return (c1, c2) -> {
			if (CollectionUtils.isEmpty(c1) && CollectionUtils.isEmpty(c2)) {
				return 0;
			} else if (CollectionUtils.isEmpty(c1)) {
				return -1;
			} else if (CollectionUtils.isEmpty(c2)) {
				return 1;
			}
			var value = Integer.compare(c1.size(), c2.size());

			if (value == 0) {
				var l1 = c1.stream().filter(Objects::nonNull).sorted(source).toList();
				var l2 = c2.stream().filter(Objects::nonNull).sorted(source).toList();

				for (int i = 0; i < l1.size(); i++) {
					value = source.compare(l1.get(i), l2.get(i));

					if (value != 0) {
						return value;
					}
				}
			}
			return value;
		};
	}

	public static String trimDashes(String source) {
		return StringUtils.trimToEmpty(StringUtils.strip(source, " -\n\t"));
	}

	public static int compareTrimmed(String s1, String s2) {
		return StringUtils.compare(StringUtils.trimToEmpty(s1), StringUtils.trimToEmpty(s2));
	}

	public static int compareTrimmedIgnoreCase(String s1, String s2) {
		return StringUtils.compareIgnoreCase(StringUtils.trimToEmpty(s1), StringUtils.trimToEmpty(s2));
	}

	public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> toMap() {
		return toMap(Map.Entry::getKey, Map.Entry::getValue);
	}

	public static <T, K2, V2> Collector<T, ?, Map<K2, V2>> toMap(Function<? super T, ? extends K2> keyMapper, Function<? super T, ? extends V2> valueMapper) {
		return Collectors.toMap(keyMapper, valueMapper, (e, r) -> e);
	}

	public static <T> Collector<T, List<List<T>>, List<List<T>>> groupMatching(BiPredicate<T, T> shouldMerge) {
		return Collector.of(ArrayList::new, (l, p) -> {
			for (var sublist : l) {
				if (sublist.stream().anyMatch(p2 -> shouldMerge.test(p, p2))) {
					sublist.add(p);
					return;
				}
			}
			l.add(new ArrayList<>(List.of(p)));
		}, (l1, l2) -> {
			l1.addAll(l2);
			return l1;
		});
	}

	public static <K extends Enum<K>, V> Map<K, V> enumMap(@Nonnull Class<K> keyClass, @Nullable Map<K, V> map) {
		return MapUtils.isEmpty(map) ? new EnumMap<>(keyClass) : new EnumMap<>(map);
	}

	public static String replaceAllWithSpace(Pattern pattern, String text, String replacement) {
		Matcher matcher = pattern.matcher(text);

		boolean result = matcher.find();

		if (result) {
			StringBuilder sb = new StringBuilder();

			do {
				var replacementSb = new StringBuilder();

				if (matcher.start() > 0) {
					replacementSb.append(' ');
				}
				replacementSb.append(replacement);
				if (matcher.end() < text.length()) {
					replacementSb.append(' ');
				}
				matcher.appendReplacement(sb, replacementSb.toString());
				result = matcher.find();
			} while (result);
			matcher.appendTail(sb);
			return sb.toString();
		}
		return text;
	}

	public static String or(String value, Supplier<String> defaultValue) {
		return StringUtils.isNotBlank(value) ? value : defaultValue.get();
	}

	public static JavaType getJavaType(ObjectMapper mapper, ParameterizedTypeReference<?> type) {
		return mapper.constructType(GenericTypeResolver.resolveType(type.getType(), (Class<?>) null));
	}

	public static JavaType getJavaType(DatabindContext context, ParameterizedTypeReference<?> type) {
		return context.constructType(GenericTypeResolver.resolveType(type.getType(), (Class<?>) null));
	}

	public static <S, T> BiConsumer<S, Consumer<T>> cast(Class<T> clazz) {
		return (s, c) -> {
			if (clazz.isInstance(s)) {
				c.accept(clazz.cast(s));
			}
		};
	}

	@Nonnull
	public static <T> List<T> fluxToList(Flux<T> flux) {
		return flux.collectList().blockOptional().orElse(Collections.emptyList());
	}

	public static String logDuration(Instant start) {
		var durationMillis = getDurationMillis(start);

		if (durationMillis > 1000) {
			return String.format("%d.%03d seconds", durationMillis / 1000, durationMillis % 1000);
		}
		return String.format("%d ms", durationMillis);
	}
	public static long getDurationMillis(Instant start) {
		return Duration.between(start, Instant.now()).toMillis();
	}

	@SafeVarargs
	public static <T> List<T> progressiveFilter(List<T> list, Predicate<T> ... predicates) {
		if (predicates.length == 0 || CollectionUtils.isEmpty(list)) {
			return list;
		}

		for (var predicate : predicates) {
			var newList = list.stream()
					.filter(predicate)
					.toList();

			if (CollectionUtils.isNotEmpty(newList)) {
				list = newList;
			}
		}
		return list;
	}

	public static <T> int getFirstIndexMatching(List<T> list, Predicate<T> predicate) {
		if (CollectionUtils.isEmpty(list)) {
			return -1;
		}

		for (int i = 0; i < list.size(); i++) {
			if (predicate.test(list.get(i))) {
				return i;
			}
		}
		return -1;
	}

	@Nonnull
	public static String clean(@Nullable String source) {
		if (StringUtils.isBlank(source)) {
			return "";
		}
		return trimDashes(source
				.replace("\u200B", " ")
				.replace("\u00a0"," ")
				.replace("\u3000"," ")
				.replaceAll("\\s+", " ")
				.replace("’", "'"));
	}
}
