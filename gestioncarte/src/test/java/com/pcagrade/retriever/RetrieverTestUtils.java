package com.pcagrade.retriever;

import com.pcagrade.mason.jpa.repository.MasonRepository;
import jakarta.persistence.Table;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings("unchecked")
public class RetrieverTestUtils {

    public static <T, ID, R extends CrudRepository<T, ID>> R mockRepository(Class<R> repositoryClass) {
        return mockRepository(repositoryClass, Collections.emptyList(), null);
    }


    public static <T, ID, R extends CrudRepository<T, ID>> R mockRepository(Class<R> repositoryClass, List<T> entities, Function<T, ID> idGetter) {
        var repository = Mockito.mock(repositoryClass);

        Mockito.when(repository.save(Mockito.any())).then(invocation -> invocation.getArgument(0));
        Mockito.when(repository.saveAll(Mockito.any())).then(invocation -> {
            Iterable<? extends T> list = invocation.getArgument(0);

            list.forEach(repository::save);
            return IterableUtils.toList(list);
        });
        if (repository instanceof JpaRepository jpaRepository) {
            Mockito.when(jpaRepository.saveAndFlush(Mockito.any())).then(invocation -> {
                T entity = invocation.getArgument(0);

                repository.save(entity);
                jpaRepository.flush();
                return entity;
            });
            Mockito.when(jpaRepository.saveAllAndFlush(Mockito.any())).then(invocation -> {
                Iterable<? extends T> list = invocation.getArgument(0);

                list.forEach(repository::save);
                jpaRepository.flush();
                return IterableUtils.toList(list);
            });
        }
        if (repository instanceof MasonRepository masonRepository) {
            Mockito.when(masonRepository.findByNullableId(Mockito.any())).thenCallRealMethod();
            Mockito.when(masonRepository.getOrCreate(Mockito.any(), Mockito.any())).thenCallRealMethod();
            Mockito.when(masonRepository.getTableName()).then(i -> CollectionUtils.isEmpty(entities) ? "" : entities.get(0).getClass().getAnnotation(Table.class).name());
        }
        if (CollectionUtils.isNotEmpty(entities)) {
            if (entities.stream()
                    .map(idGetter)
                    .filter(Objects::nonNull)
                    .distinct()
                    .count() != entities.size()) {
                throw new IllegalArgumentException("Error mocking repository: " + repositoryClass.getName() + ". The list of entities contains duplicated IDs: " + entities.stream()
                        .map(idGetter)
                        .filter(Objects::nonNull)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                        .entrySet().stream()
                        .filter(e -> e.getValue() > 1)
                        .map(e -> e.getKey().toString())
                        .collect(Collectors.joining(", ", "[", "]")));
            }

            Mockito.when(repository.findAll()).thenReturn(entities);
            Mockito.when(repository.findById(Mockito.any())).then(i -> {
                ID id = i.getArgument(0);

                return entities.stream()
                        .filter(e -> Objects.equals(id, idGetter.apply(e)))
                        .findFirst();
            });
            Mockito.when(repository.findAllById(Mockito.any(Iterable.class))).then(invocation -> {
                Iterable<? extends ID> ids = invocation.getArgument(0);

                return StreamSupport.stream(ids.spliterator(), false)
                        .map(id -> repository.findById(id).orElse(null))
                        .filter(Objects::nonNull)
                        .toList();
            });
        }
        return repository;
    }

    public static <T> T spyLambda(final Class<? super T> lambdaType, final T lambda) {
        return (T) Mockito.mock(lambdaType, AdditionalAnswers.delegatesTo(lambda));
    }
}
