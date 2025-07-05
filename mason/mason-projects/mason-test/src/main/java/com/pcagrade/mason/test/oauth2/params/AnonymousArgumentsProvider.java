package com.pcagrade.mason.test.oauth2.params;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.util.stream.Stream;

public class AnonymousArgumentsProvider implements IOAuth2ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        check(extensionContext, AnonymousRequestSource.class);
        return Stream.of(Arguments.of(SecurityMockMvcRequestPostProcessors.anonymous()));
    }
}
