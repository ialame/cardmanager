package com.pcagrade.retriever.parser.wiki.result.ask;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.parser.wiki.result.ask.printout.IWikiAskPrintout;
import com.pcagrade.retriever.parser.wiki.result.ask.printout.WikiAskPrintoutNumber;
import com.pcagrade.retriever.parser.wiki.result.ask.printout.WikiAskPrintoutPage;
import com.pcagrade.retriever.parser.wiki.result.ask.printout.WikiAskPrintoutText;
import org.springframework.core.ParameterizedTypeReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WikiAskDeserializer extends StdDeserializer<WikiAskQuery> {

    private static final ParameterizedTypeReference<List<WikiAskPrintRequest>> PRINT_REQUESTS_TYPE = new ParameterizedTypeReference<>() {
    };

    public WikiAskDeserializer() {
        super(WikiAskQuery.class);
    }

    @Override
    public WikiAskQuery deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        var node = context.readTree(parser).get("query");
        var printRequests = context.<List<WikiAskPrintRequest>>readTreeAsValue(node.get("printrequests"), PCAUtils.getJavaType(context, PRINT_REQUESTS_TYPE));
        var typeMap = printRequests.stream().collect(PCAUtils.toMap(WikiAskPrintRequest::key, r -> getClass(r.typeId())));
        var resultsNode = node.get("results");
        var results = new HashMap<String, WikiAskResult>();
        var fields = resultsNode.fields();

        while (fields.hasNext()) {
            var entry = fields.next();
            var result = parseResult(entry.getValue(), context, typeMap);
            results.put(entry.getKey(), result);
        }
        return new WikiAskQuery(printRequests, results);
    }

    private WikiAskResult parseResult(JsonNode node, DeserializationContext context, Map<String, Class<IWikiAskPrintout>> typeMap) throws IOException {
        var root = context.readTreeAsValue(node, typeMap.get(""));
        var printouts = new HashMap<String, List<IWikiAskPrintout>>();
        var fields = node.get("printouts").fields();

        while (fields.hasNext()) {
            var entry = fields.next();
            var key = entry.getKey();

            printouts.put(key, context.readTreeAsValue(entry.getValue(), context.getTypeFactory().constructCollectionType(List.class, typeMap.get(key))));
        }
        return new WikiAskResult(root, printouts);
    }

    @SuppressWarnings("unchecked")
    private <T extends IWikiAskPrintout> Class<T> getClass(String typeId) {
        return (Class<T>) switch (typeId) {
            case "_wpg" -> WikiAskPrintoutPage.class;
            case "_txt" -> WikiAskPrintoutText.class;
            case "_num" -> WikiAskPrintoutNumber.class;
            default -> throw new IllegalArgumentException("Unknown typeId: " + typeId);
        };
    }
}
