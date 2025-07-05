package com.pcagrade.retriever.extraction.consolidation.consolidator;

import com.pcagrade.retriever.extraction.consolidation.ConsolidationException;
import com.pcagrade.retriever.extraction.consolidation.ConsolidationHelper;
import com.pcagrade.retriever.extraction.consolidation.ConsolidationService;
import com.pcagrade.retriever.extraction.consolidation.source.IConsolidationSource;
import org.springframework.core.ResolvableType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecordConsolidator implements IConsolidator {
    @Override
    public boolean canConsolidate(ResolvableType type) {
        return ConsolidationHelper.getRawClass(type).isRecord();
    }

    @Override
    public Object consolidate(ResolvableType type, ConsolidationService service, List<? extends IConsolidationSource<?>> sources) {
        var recordClass = ConsolidationHelper.getRawClass(type);
        var recordComponents = recordClass.getRecordComponents();
        var recordBuilder = new RecordBuilder(recordClass);

        for (var component : recordComponents) {
            recordBuilder.addValue(component, service.consolidate((Type) ResolvableType.forType(component.getGenericType()), sources.stream()
                    .map(s -> {
                        try {
                            return s.with(component.getAccessor().invoke(s.getValue()));
                        } catch (IllegalAccessException  | InvocationTargetException e) {
                            throw error(recordClass, e);
                        }
                    })
                    .toList()));
        }
        return recordBuilder.build();
    }

    private static ConsolidationException error(Class<?> recordClass, ReflectiveOperationException e) {
        return new ConsolidationException("Error while consolidating record: " + recordClass, e);
    }

    private static class RecordBuilder {

        private final Class<?> recordClass;
        private final Map<RecordComponent, Object> values;

        private RecordBuilder(Class<?> recordClass) {
            this.recordClass = recordClass;
            values = new LinkedHashMap<>();
        }

        public void addValue(RecordComponent component, Object value) {
            values.put(component, value);
        }

        public Object build() {
            try {
                return recordClass.getConstructor(values.keySet().stream().map(RecordComponent::getType).toArray(Class[]::new)).newInstance(values.values().toArray());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw error(recordClass, e);
            }
        }

    }
}
