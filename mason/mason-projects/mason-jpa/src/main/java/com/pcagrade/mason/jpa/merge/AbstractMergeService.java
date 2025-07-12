package com.pcagrade.mason.jpa.merge;

import com.pcagrade.mason.commons.FilterHelper;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import com.pcagrade.mason.jpa.revision.HistoryTreeDTO;
import com.pcagrade.mason.jpa.revision.RevisionDTO;
import com.pcagrade.mason.jpa.revision.message.RevisionMessageService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.util.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
public abstract class AbstractMergeService<T, I extends Serializable> {

    private static final Logger LOGGER = LogManager.getLogger(AbstractMergeService.class);

    private final MasonRevisionRepository<T, I> repository;
    private final Function<T, I> idGetter;
    private final Lazy<String> tableName;

    private final IMergeHistoryService<I> mergeHistoryService;
    private final RevisionMessageService revisionMessageService;

    protected AbstractMergeService(@Nonnull MasonRevisionRepository<T, I> repository, @Nonnull IMergeHistoryService<I> mergeHistoryService, @Nullable RevisionMessageService revisionMessageService, @Nonnull Function<T, I> idGetter) {
        this.repository = repository;
        this.mergeHistoryService = mergeHistoryService;
        this.revisionMessageService = revisionMessageService;
        this.idGetter = t -> t != null ? idGetter.apply(t) : null;
        this.tableName = Lazy.of(repository::getTableName);
    }

    public I getCurrentId(I id) {
        return mergeHistoryService.getCurrentId(tableName.get(), id);
    }

    public List<I> getMergedIds(I id) {
        return mergeHistoryService.getMergedIds(tableName.get(), id);
    }

    public I merge(List<I> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        return merge(ids.getFirst(), ids.subList(1, ids.size()));
    }

    public I merge(I targetId, List<I> ids) {
        ids = ids.stream()
                .filter(id -> !Objects.equals(id, targetId))
                .toList();

        if (CollectionUtils.isEmpty(ids)) {
            return targetId;
        } else if (targetId == null) {
            return merge(ids);
        }

        var entities = repository.findAllById(ids);

        if (entities.isEmpty()) {
            return targetId;
        }

        var target = repository.findById(targetId).orElseThrow();

        entities.forEach(source -> {
            LOGGER.trace("Merging entities of table {}: {} to {}", tableName::get, () -> idGetter.apply(source), () -> idGetter.apply(target));
            merge(target, source);
            repository.delete(source);
            mergeHistoryService.addMergeHistory(tableName.get(), idGetter.apply(source), idGetter.apply(target));
        });

        var id = idGetter.apply(repository.save(target));

        if (revisionMessageService != null) {
            revisionMessageService.addMessage("Fusion des entités de la table {0}: {1} dans {2}", tableName.get(),
                    ids.stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(", ")), id);
        }
        return id;
    }

    protected abstract void merge(T target, T source);

    public HistoryTreeDTO<T> getHistoryTreeById(I id) {
        return doGetHistoryTree(getCurrentId(id));
    }

    private HistoryTreeDTO<T> doGetHistoryTree(I id) {
        var rev = getRevisionsById(id);

        if (CollectionUtils.isEmpty(rev)) {
            return null;
        }

        var history = HistoryTreeDTO.fromRevisions(rev);
        var otherIds = getMergedIds(id);

        for (var otherId : otherIds) {
            var tree = doGetHistoryTree(otherId);

            if (tree != null) {
                history = history.merge(tree, e -> Objects.equals(idGetter.apply(e), id));
            }

        }
        return history;
    }

    private List<RevisionDTO<T>> getRevisionsById(I id) {
        return repository.findRevisions(id).stream()
                .map(RevisionDTO::of)
                .filter(FilterHelper.distinctFromPrevious())
                .toList();
    }
}
