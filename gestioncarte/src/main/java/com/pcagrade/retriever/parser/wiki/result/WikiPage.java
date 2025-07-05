package com.pcagrade.retriever.parser.wiki.result;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiPage<T extends WikiPageContent>(
        @JsonAlias({"pageid", "_pageid"})
        int pageId,
        int ns,
        String title,
        List<T> revisions
) { }
