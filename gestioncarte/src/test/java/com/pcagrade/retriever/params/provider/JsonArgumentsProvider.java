package com.pcagrade.retriever.params.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.stream.Stream;

public class JsonArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<JsonSource> {

    private String[] resources;

    @Override
    public void accept(JsonSource jsonSource) {
        this.resources = jsonSource.value();
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        var applicationContext = SpringExtension.getApplicationContext(context);
        var objectMapper = applicationContext.getBean(ObjectMapper.class);
        var type = context.getTestMethod().orElseThrow().getParameters()[0].getType();

        return Arrays.stream(resources).map(s -> {
            var resource = applicationContext.getResource("classpath:json/" + s + ".json");

            if (resource.exists()) {
                try {
                    return Arguments.of(objectMapper.readValue(resource.getInputStream(), type));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            throw new RuntimeException("Resource " + s + " not found");
        });
    }
}
