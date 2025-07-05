package com.pcagrade.retriever.parser.wiki.result.ask.printout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikiAskPrintoutText(
        @JsonValue
        String value
) implements IWikiAskPrintout { }
