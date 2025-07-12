package com.pcagrade.mason.test.oauth2.params;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.platform.commons.util.Preconditions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.lang.reflect.Method;

public interface IOAuth2ArgumentsProvider extends ArgumentsProvider {

    default void check(ExtensionContext extensionContext, Class<?> annotationType) {
        Method testMethod = extensionContext.getRequiredTestMethod();
        Preconditions.condition(
                testMethod.getParameterCount() == 1 && testMethod.getParameterTypes()[0].isAssignableFrom(RequestPostProcessor.class),
                () -> String.format("@%s cannot provide a RequestPostProcessor to method [%s]: the method does not declare the correct parameters.", annotationType.getSimpleName(), testMethod.toGenericString())
        );
    }
}
