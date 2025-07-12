package com.pcagrade.retriever.parser.wiki.result.ask;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiAskPrintRequest(
        String label,
        String key,
        String redi,
        @JsonAlias("typeid")
        String typeId,
        int mode,
        String format
) { }
