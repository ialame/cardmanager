package com.pcagrade.retriever.parser.wiki.result;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiCategoryMember(
        @JsonAlias({"pageid", "_pageid"})
        int pageId,
        @JsonAlias({"ns", "_ns"})
        int ns,
        @JsonAlias({"title", "_title"})
        String title
) {
}
