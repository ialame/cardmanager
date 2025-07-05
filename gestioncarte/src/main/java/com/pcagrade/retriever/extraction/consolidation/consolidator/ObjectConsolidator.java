package com.pcagrade.retriever.extraction.consolidation.consolidator;

import com.pcagrade.retriever.extraction.consolidation.ConsolidationException;
import com.pcagrade.retriever.extraction.consolidation.ConsolidationHelper;
import com.pcagrade.retriever.extraction.consolidation.ConsolidationService;
import com.pcagrade.retriever.extraction.consolidation.source.IConsolidationSource;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.core.ResolvableType;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectConsolidator implements IConsolidator {
    @Override
    public boolean canConsolidate(ResolvableType type) {
        return true;
    }

    @Override
    public Object consolidate(ResolvableType type, ConsolidationService service, List<? extends IConsolidationSource<?>> sources) {
        var proxyFactory = new ProxyFactory();
        var rawClass = ConsolidationHelper.getRawClass(type);

        try {
            proxyFactory.setTargetClass(rawClass);
            proxyFactory.setTarget(rawClass.getConstructor().newInstance());
            proxyFactory.setProxyTargetClass(true);
            proxyFactory.addAdvice(new ConsolidationInterceptor(service, sources));
            return proxyFactory.getProxy(rawClass.getClassLoader());
        } catch (Exception e) {
            throw new ConsolidationException("Failed to create proxy for " + rawClass, e);
        }
    }

    private static class ConsolidationInterceptor implements MethodInterceptor {

        private final ConsolidationService service;
        private final List<? extends IConsolidationSource<?>> sources;
        private final Map<String, Object> values;

        private ConsolidationInterceptor(ConsolidationService service, List<? extends IConsolidationSource<?>> sources) {
            this.service = service;
            this.sources = sources;
            values = new HashMap<>();
        }

        private String getPropertyKey(String methodName) {
            if (StringUtils.startsWith(methodName, "is")) {
                return StringUtils.uncapitalize(StringUtils.substring(methodName, 2));
            }
            return StringUtils.uncapitalize(StringUtils.substring(methodName, 3));
        }

        private boolean isGetter(Method method) {
            if (method.getName().startsWith("get")) {
                return method.getParameterCount() == 0 && !method.getReturnType().equals(void.class);
            } else if (method.getName().startsWith("is")) {
                return method.getParameterCount() == 0 && (method.getReturnType().equals(boolean.class) || method.getReturnType().equals(Boolean.class));
            }
            return false;
        }

        private boolean isSetter(Method method) {
            return method.getName().startsWith("set") && method.getParameterCount() == 1 && method.getReturnType().equals(void.class);
        }

        @Nullable
        @Override
        public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
            var method = invocation.getMethod();
            var name = method.getName();

            if (isGetter(method)) {
                var propertyKey = getPropertyKey(name);

                if (values.containsKey(propertyKey)) {
                    return values.get(propertyKey);
                }
                var value = consolidateValue(method);

                values.put(propertyKey, value);
                return value;
            } else if (isSetter(method)) {
                values.put(getPropertyKey(name), invocation.getArguments()[0]);
            }
            return invocation.proceed();
        }

        @Nullable
        private Object consolidateValue(Method method) {
            return service.consolidateValue(ResolvableType.forMethodReturnType(method), sources.stream()
                    .<IConsolidationSource<?>>mapMulti((s, downstream) -> {
                        var v = safeInvokeMethod(method, s.getValue());

                        if (v != null) {
                            downstream.accept(s.with(v));
                        }
                    }).toList());
        }

        private Object safeInvokeMethod(Method method, Object source) {
            try {
                return method.invoke(source);
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
