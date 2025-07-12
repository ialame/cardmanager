package com.pcagrade.retriever.parser.wiki.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiQuery<T extends WikiPageContent>(
        List<WikiNormalized> normalized,
        Map<String, WikiPage<T>> pages
) { }
