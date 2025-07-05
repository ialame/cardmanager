package com.pcagrade.retriever.parser.wiki.result.ask.printout;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiAskPrintoutPage(
        @JsonAlias("fulltext")
        String fullText,
        @JsonAlias("fullurl")
        String fullUrl,
        int namespace,
        String exists,
        @JsonAlias("displaytitle")
        String displayTitle
) implements IWikiAskPrintout { }
