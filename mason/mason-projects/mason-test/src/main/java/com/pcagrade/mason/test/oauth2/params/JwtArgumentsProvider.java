package com.pcagrade.mason.test.oauth2.params;

import com.pcagrade.mason.test.oauth2.OAuth2PostProcessors;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.AnnotationBasedArgumentsProvider;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;
import java.util.stream.Stream;

public class JwtArgumentsProvider extends AnnotationBasedArgumentsProvider<JwtRequestSources> implements IOAuth2ArgumentsProvider {

    @Override
    protected Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext, JwtRequestSources jwtRequestSources) {
        check(extensionContext, JwtRequestSource.class);

        return Arrays.stream(jwtRequestSources.value())
                .map(this::arguments);
    }

    private Arguments arguments(JwtRequestSource jwtRequestSource) {
        return Arguments.of(OAuth2PostProcessors.jwtWithRoles(jwtRequestSource.value()));
    }
}
