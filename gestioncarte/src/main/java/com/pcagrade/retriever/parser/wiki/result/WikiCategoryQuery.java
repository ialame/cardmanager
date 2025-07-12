package com.pcagrade.retriever.parser.wiki.result;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiCategoryQuery(
        @JsonAlias("categorymembers")
        List<WikiCategoryMember> categoryMembers
) {
}
