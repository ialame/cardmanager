package com.pcagrade.mason.ulid;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.f4b6a3.ulid.Ulid;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@AutoConfigurationPackage
public class UlidAutoConfiguration {

    @Bean
    public UlidConverter ulidConverter() {
        return new UlidConverter();
    }

    @Bean
    @ConditionalOnMissingBean
    public UlidMapper ulidMapper() {
        return new UlidMapper();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({JsonSerializer.class, JsonDeserializer.class})
    public static class Jackson {

        @Bean
        public Module ulidModule(UlidMapper mapper) {
            var module = new SimpleModule("UlidModule");

            module.addSerializer(Ulid.class, new StdSerializer<>(Ulid.class) {
                @Override
                public void serialize(Ulid value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeString(mapper.toString(value));
                }
            });
            module.addDeserializer(Ulid.class, new StdDeserializer<>(Ulid.class) {
                @Override
                public Ulid deserialize(JsonParser p, DeserializationContext context) throws IOException {
                    return mapper.fromString(p.getValueAsString());
                }
            });
            return module;
        }
    }
}
