package com.pcagrade.mason.jpa.revision;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.history.RevisionMetadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public record HistoryTreeDTO<T>(
        RevisionDTO<T> revision,
        List<HistoryTreeDTO<T>> parents
) {

    public HistoryTreeDTO {
        parents = parents.stream()
                .sorted(Comparator.comparingInt(h -> h.revision().number()))
                .toList();
    }

    public HistoryTreeDTO(RevisionDTO<T> revision) {
        this(revision, Collections.emptyList());
    }

    public HistoryTreeDTO(RevisionDTO<T> revision, HistoryTreeDTO<T> parent) {
        this(revision, List.of(parent));
    }

    public static <T> HistoryTreeDTO<T> fromRevisions(Collection<RevisionDTO<T>> revision) {
        if (CollectionUtils.isEmpty(revision)) {
            throw new IllegalArgumentException("Revision collection must not be empty");
        }

        var sorted = revision.stream()
                .sorted(Comparator.comparingInt(RevisionDTO::number))
                .toList();
        var current = new HistoryTreeDTO<>(sorted.get(0));

        for (var i = 1; i < sorted.size(); i++) {
            current = new HistoryTreeDTO<>(sorted.get(i), current);
        }
        return current;
    }

    public HistoryTreeDTO<T> merge(HistoryTreeDTO<T> other, Predicate<T> canMerge) {
        if (other.revision.number() >= revision.number()) {
            if (other.revision.type() == RevisionMetadata.RevisionType.DELETE) {
                var list = new ArrayList<HistoryTreeDTO<T>>();

                list.add(this);
                list.addAll(other.parents);
                return new HistoryTreeDTO<>(other.revision.map(e -> revision.data()), list);
            }
            return new HistoryTreeDTO<>(revision, List.of(this, other));
        }

        if (parents.isEmpty() && canMerge.test(this.revision.data())) {
            return new HistoryTreeDTO<>(revision, other);
        }
        return new HistoryTreeDTO<>(revision, parents.stream()
                .map(p -> {
                    if (canMerge.test(p.revision.data())) {
                        return p.merge(other, canMerge);
                    }
                    return p;
                }).toList());
    }

    public <U> HistoryTreeDTO<U> map(Function<T, U> mapper) {
        return new HistoryTreeDTO<>(revision.map(mapper), parents.stream()
                .map(p -> p.map(mapper))
                .toList());
    }
}
