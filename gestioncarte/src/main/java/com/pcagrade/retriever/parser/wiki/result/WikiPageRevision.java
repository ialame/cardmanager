package com.pcagrade.retriever.parser.wiki.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiPageRevision(
        Map<String, WikiPageSlot> slots
) implements WikiPageContent {
    @Override
    @JsonIgnore
    public String content() {
        return slots.get("main").content();
    }
}
