package com.pcagrade.retriever.parser;

import com.pcagrade.retriever.image.IImageParser;
import jakarta.annotation.Nonnull;
import org.jsoup.nodes.Document;
import reactor.core.publisher.Mono;

import java.io.IOException;

public interface IHTMLParser extends IImageParser {
    @Nonnull
    default Document get(String url) throws IOException {
        try {
            var value = getMono(url).block();

            if (value == null) {
                throw new IOException("Could not parse HTML document for URL: " + url);
            }
            return value;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    Mono<Document> getMono(String url);

    default String processUrl(String baseUrl, String url) {
        return url.startsWith("http") ? url : baseUrl + url;
    }
}
