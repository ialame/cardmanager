package com.pcagrade.retriever.data;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractListDataManager<E, D, T, I> {

    private static final Logger LOGGER = LogManager.getLogger(AbstractListDataManager.class);

    public List<D> getList(I targetId) {
        return getByTargetId(targetId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public void setList(I targetId, List<D> dtos) {
        if (targetId == null) {
            return;
        }
        var opt = findTargetById(targetId);

        if (opt.isEmpty()) {
            LOGGER.error("Target with id {} not found", targetId);
            return;
        }

        var set = opt.get();
        var news = dtos.stream()
                .filter(this::isValid)
                .collect(Collectors.toList());

        deleteAll(getByTargetId(targetId).stream()
                .filter(p -> {
                    var pid = mapToDTO(p);

                    return !news.removeIf(p2 -> equals(p2, pid));
                }).toList());

        news.forEach(p -> {
            var e = mapToEntity(p, set);

            save(e);
            LOGGER.trace("Saving data '{}' for target {}", p, targetId);
        });
    }

    protected boolean isValid(D dto) {
        if (dto == null) {
            return false;
        } else if (dto instanceof String s) {
            return StringUtils.isNotBlank(s);
        } else if (dto instanceof Number n) {
            return n.doubleValue() > 0;
        }
        return true;
    }
    protected boolean equals(D dto1, D dto2) {
        return Objects.equals(dto1, dto2);
    }

    protected abstract List<E> getByTargetId(I targetId);
    protected abstract D mapToDTO(E entity);
    protected abstract E mapToEntity(D dto, T target);
    protected abstract Optional<T> findTargetById(I targetId);
    protected abstract void deleteAll(List<E> entities);
    protected abstract void save(E entity);
}
