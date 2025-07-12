package com.pcagrade.mason.commons;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class MapMultiHelper {

    private MapMultiHelper() { }

    public static <T, R> BiConsumer<T, Consumer<R>> mapOptional(Function<T, Optional<R>> mapper) {
        return (t, downstream) -> mapper.apply(t).ifPresent(downstream);
    }

    public static <T, R> BiConsumer<T, Consumer<R>> mapCollection(Function<T, Collection<R>> mapper) {
        return (t, downstream) -> mapper.apply(t).forEach(downstream);
    }

}
