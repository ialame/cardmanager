package com.pcagrade.retriever.parser.wiki.result;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiPageResult<T extends WikiPageContent>(
        @JsonAlias("batchcomplete")
        String batchComplete,
        WikiQuery<T> query
) { }
