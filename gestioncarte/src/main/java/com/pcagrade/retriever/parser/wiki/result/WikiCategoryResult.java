package com.pcagrade.retriever.parser.wiki.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiCategoryResult(
        WikiCategoryQuery query
) {
}
