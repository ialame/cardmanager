package com.pcagrade.painer.client;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrade.mason.localization.LocalizationAutoConfiguration;
import com.pcagrade.mason.ulid.UlidAutoConfiguration;
import com.pcagrade.mason.web.client.WebClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;

@EnablePainter
@Import({ WebClientAutoConfiguration.class, UlidAutoConfiguration.class, LocalizationAutoConfiguration.class })
public class PainterTestConfiguration {

    @Bean
    public ObjectMapper objectMapper(List<Module> modules) {
        var mapper = new ObjectMapper();

        mapper.registerModules(modules);
        mapper.findAndRegisterModules();
        return mapper;
    }
}
