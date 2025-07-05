package com.pcagrade.retriever.parser.wiki.result.ask;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiAskQuery(
        @JsonAlias("printrequests")
        List<WikiAskPrintRequest> printRequests,
        Map<String, WikiAskResult> results
) { }
