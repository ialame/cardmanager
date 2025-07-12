package com.pcagrade.retriever.card.yugioh.source.ygoprodeck;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.eclipse.jgit.util.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class YgoProDeckJacksonModule extends SimpleModule {
    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.addDeserializationProblemHandler(new YgoProDeckDeserializationProblemHandler());
    }

    private static class YgoProDeckDeserializationProblemHandler extends DeserializationProblemHandler {
        @Override
        public Object handleWeirdStringValue(DeserializationContext context, Class<?> targetType, String valueToConvert, String failureMsg) throws IOException {
            if (StringUtils.equalsIgnoreCase(valueToConvert, "0000-00-00")) {
                return null;
            }
            return super.handleWeirdStringValue(context, targetType, valueToConvert, failureMsg);
        }
    }
}
