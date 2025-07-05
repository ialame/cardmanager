package com.pcagrade.retriever.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.extraction.consolidation.source.IConsolidationSource;
import com.pcagrade.mason.ulid.UlidHelper;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;

import jakarta.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

public abstract class AbstractExtractionService<T extends AbstractExtractedDTO> implements InitializingBean, DisposableBean, MeterRegistryCustomizer<MeterRegistry> {

    private static final Logger LOGGER = LogManager.getLogger(AbstractExtractionService.class);

    private final String name;
    private final ObjectMapper mapper;
    private final Class<T[]> type;
    private final ArrayList<T> extracted;

    @Value("${retriever.extraction.save.folder:}")
    private String saveFolder;

    @SuppressWarnings("unchecked")
    protected AbstractExtractionService(String name, ObjectMapper mapper, Class<T> type) {
        this.name = name;
        this.mapper = mapper;
        this.type = (Class<T[]>) type.arrayType();
        extracted = new ArrayList<>();
    }

    @Override
    public void afterPropertiesSet() {
        var saveFile = getSaveFile();

        if (StringUtils.isNotBlank(saveFile)) {
            var file = new File(saveFile);

            if (file.exists()) {
                synchronized (extracted) {
                    try {
                        extracted.addAll(Arrays.asList(mapper.readValue(file, type)));
                        LOGGER.info("Loaded {} extracted {} from disk ({})", extracted::size, () -> name, file::getAbsolutePath);
                    } catch (IOException e) {
                        LOGGER.error(() -> "Failed to load extracted " + name + " from disk", e);
                    }
                }
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        var saveFile = getSaveFile();

        if (StringUtils.isNotBlank(saveFile)) {
            synchronized (extracted) {
                var file = new File(saveFile);

                if (!extracted.isEmpty()) {
                    var folder = file.getParentFile();

                    if (!folder.exists() && !folder.mkdirs()) {
                        throw new IOException("Unable to create folder: " + folder.getAbsolutePath());
                    }
                    mapper.writeValue(file, extracted.toArray());
                    LOGGER.info("Saved {} extracted {} to disk ({})", extracted::size, () -> name, file::getAbsolutePath);
                } else if (file.exists()) {
                    Files.delete(file.toPath());
                }
            }
        }
    }

    protected void put(List<T> list, Comparator<T> comparator) {
        synchronized (extracted) {
            extracted.ensureCapacity(extracted.size() + list.size());
            list.forEach(c -> {
                if (extracted.stream().noneMatch(c2 -> comparator.compare(c, c2) == 0)) {
                    if (c.getId() == null) {
                        c.setId(UlidCreator.getUlid());
                    }
                    extracted.add(c);
                }
            });
        }
    }

    @Nonnull
    protected static <T> List<List<IConsolidationSource<T>>> createConsolidationGroups(List<? extends List<? extends IConsolidationSource<T>>> lists, BiPredicate<T, T> shouldGroup) {
        LOGGER.debug("Creating consolidation groups from {} lists", lists::size);

        var groups = new ArrayList<List<IConsolidationSource<T>>>(lists.stream()
                .mapToInt(List::size)
                .max()
                .orElse(0));

        for (var sources : lists) {
            for (var source : sources) {
                var value = source.getValue();
                List<IConsolidationSource<T>> group = null;

                for (var it = groups.iterator(); it.hasNext();) {
                    var g = it.next();

                    if (areSameGroup(source, g, group) && g.stream().anyMatch(s -> shouldGroup.test(s.getValue(), value))) {
                        if (group != null) {
                            group.addAll(g);
                            it.remove();
                        } else {
                            g.add(source);
                            group = g;
                        }
                    }
                }
                if (group == null) {
                    group = new ArrayList<>();

                    group.add(source);
                    groups.add(group);
                }
            }
        }
        return groups;
    }

    private static <T> boolean areSameGroup(IConsolidationSource<T> source, List<IConsolidationSource<T>> g, List<IConsolidationSource<T>> group) {
        return g.stream().noneMatch(s -> areSameSource(s, source) || (group != null && group.stream().anyMatch(s2 -> areSameSource(s, s2))));
    }

    private static boolean areSameSource(IConsolidationSource<?> s1, IConsolidationSource<?> s2) {
        return s1.isSameSource(s2) || s2.isSameSource(s1);
    }

    public List<T> get() {
        synchronized (extracted) {
            return List.copyOf(extracted);
        }
    }

    public void clear() {
        synchronized (extracted) {
            extracted.clear();
        }
    }

    public T save(final T data) {
        synchronized (extracted) {
            if (data.getId() == null) {
                data.setId(UlidCreator.getUlid());
            }

            var id = data.getId();
            var index = getIndex(id);

            if (index >= 0) {
                extracted.set(index, data);
            } else {
                extracted.add(data);
            }
            return data;
        }
    }

    public T delete(Ulid id) {
        synchronized (extracted) {
            var index = getIndex(id);

            if (index >= 0) {
                return extracted.remove(index);
            }
        }
        return null;
    }

    private int getIndex(Ulid id) {
        synchronized (extracted) {
            return PCAUtils.getFirstIndexMatching(extracted, c -> UlidHelper.equals(c.getId(), id));
        }
    }

    @Override
    public void customize(MeterRegistry registry) {
        Gauge.builder("extracted." + name, extracted, List::size)
                .description("Number of extracted " + name)
                .register(registry);
    }

    private String getSaveFile() {
        return saveFolder + "/" + name + ".json";
    }

    public Optional<T> get(Ulid id) {
        synchronized (extracted) {
            return extracted.stream()
                    .filter(c -> UlidHelper.equals(c.getId(), id))
                    .findFirst();
        }
    }
}
