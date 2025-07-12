package com.pcagrade.retriever.parser.wiki.result;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiPageSlot(
        @JsonAlias("contentmodel")
        String contentModel,
        @JsonAlias("contentformat")
        String contentFormat,
        @JsonAlias("*")
        String content
) implements WikiPageContent { }
