package com.pcagrade.retriever.localization.translation;

import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.PCAUtils;
import jakarta.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractTranslationMapper<E extends AbstractTranslationEntity<?>, D extends ILocalized> {
	
	private final Class<E> type;
	
	protected AbstractTranslationMapper(Class<E> type) {
		this.type = type;
	}

	@Nonnull
	protected Map<Localization, D> translationMapToDtoMap(Map<Localization, E> translations, Function<? super E, D> mapFunction) {
		return updateMap(translations, (l, t) -> {
			D dto = mapFunction.apply(t);

			dto.setLocalization(l);
			return dto;
		});
	}

	@Nonnull
	public Map<Localization, E> dtoMapToTranslationMap(Map<Localization, ? extends D> map, Function<? super D, E> mapFunction) {
		return updateMap(map, (l, t) -> {
			E translation = mapFunction.apply(t);

			translation.setLocalization(l);
			return translation;
		});
	}

	@Nonnull
	public Map<Localization, E> update(Map<Localization, E> translations, Map<Localization, D> map, BiConsumer<E, D> update, Supplier<E> supplier) {
		return updateMap(map, (l, t) -> {
			var old = translations.get(l);
			var translation = old == null ? supplier.get() : old;

			translation.setLocalization(l);
			update.accept(translation, t);
			return translation;
		});
	}

	public static <T> Map<Localization, T> createMap(Map<Localization, T> map) {
		return PCAUtils.enumMap(Localization.class, map);
	}

	@Nonnull
	private static <U extends ILocalized, V extends ILocalized> Map<Localization, U> updateMap(Map<Localization, ? extends V> map, BiFunction<Localization, V, U> mapFunction) {
		return createMap(map.entrySet().stream()
				.map(e -> Map.entry(e.getKey(), mapFunction.apply(e.getKey(), e.getValue())))
				.collect(PCAUtils.toMap()));
	}

	@Deprecated(forRemoval = true)
	protected Map<Localization, D> translationListToDtoMap(List<?> translations, Function<? super E, D> mapFunction) {
		return translations.stream()
				.filter(type::isInstance)
				.map(type::cast)
				.map(mapFunction)
				.collect(Collectors.toMap(ILocalized::getLocalization, Function.identity()));
	}

	@Deprecated(forRemoval = true)
	protected List<E> dtoMapToTranslationList(Map<Localization, ? extends D> map, Function<? super D, E> mapFunction) {
		return map.entrySet().stream()
				.map(e -> {
					E translation = mapFunction.apply(e.getValue());

					translation.setLocalization(e.getKey());
					return translation;
				}).toList();
	}

	@Deprecated(forRemoval = true)
	public List<E> update(Collection<E> translations, Map<Localization, D> map, BiConsumer<E, D> update, Supplier<E> supplier) {
		var newList = new ArrayList<E>(map.size());

		map.forEach((localization, dto) -> {
			var translation = translations.stream()
					.filter(t -> t.getLocalization().equals(localization))
					.findFirst()
					.orElseGet(supplier);

			translation.setLocalization(localization);
			update.accept(translation, dto);
			newList.add(translation);
		});
		return newList;
	}
}
