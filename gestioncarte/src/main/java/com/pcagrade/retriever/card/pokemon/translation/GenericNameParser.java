package com.pcagrade.retriever.card.pokemon.translation;

import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.pokemon.feature.FeatureService;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
public class GenericNameParser {

    @Autowired
    private FeatureService featureService;

    public Pair<String, String> parseName(Element td, String name, Localization localization) {
        return getCardLines(td)
                .map(cardLine -> {
                    List<String> cardNameParts = new ArrayList<>(cardLine.size());
                    List<String> labelNameParts = new ArrayList<>(cardLine.size());

                    for (Node node : cardLine) {
                        if (node instanceof Element element) {
                            var tagName = element.tagName();

                            if (tagName.equalsIgnoreCase("a") || tagName.equalsIgnoreCase("img")) {
                                String text = PCAUtils.clean(element.text());
                                String title = element.attr("title");
                                String link = element.attr("href");

                                if (StringUtils.isBlank(link)) {
                                    link = element.attr("src");
                                }
                                if (StringUtils.isNotBlank(link) || StringUtils.isNotBlank(title)) {
                                    var feature = featureService.findFeature(name, title, sanitizeLink(link));

                                    if (feature != null) {
                                        var translation = feature.getTranslations().get(localization);

                                        cardNameParts.add(translation.getName());
                                        labelNameParts.add(translation.getZebraName());
                                    } else if (StringUtils.isNotBlank(text)) {
                                        cardNameParts.add(text);
                                        labelNameParts.add(text);
                                    } else if (StringUtils.isNotBlank(title)) {
                                        var trimmed = PCAUtils.clean(title);

                                        if (StringUtils.isNotBlank(trimmed)) {
                                            cardNameParts.add(trimmed);
                                            labelNameParts.add(trimmed);
                                        }
                                    }
                                } else if (StringUtils.isNotBlank(text)) {
                                    cardNameParts.add(text);
                                    labelNameParts.add(text);
                                }
                            } else if (tagName.equalsIgnoreCase("span")) {
                                String text = PCAUtils.clean(element.text());

                                if (StringUtils.isNotBlank(text)) {
                                    cardNameParts.add(text);
                                    labelNameParts.add(text);
                                }
                            }
                        } else if (node instanceof TextNode textNode) {
                            String text = PCAUtils.clean(textNode.text());

                            if (StringUtils.isNotBlank(text)) {
                                cardNameParts.add(text);
                                labelNameParts.add(text);
                            }
                        }
                    }
                    return Pair.of(PCAUtils.clean(StringUtils.join(cardNameParts, " ")), PCAUtils.clean(StringUtils.join(labelNameParts, " ")));
                })
                .filter(p -> StringUtils.isNoneBlank(p.getLeft(), p.getRight()))
                .findFirst()
                .orElse(null);
    }

    public List<Pair<String, String>> searchPatterns(Element td) {
        return getCardLines(td)
                .<Pair<String, String>>mapMulti((cardLine, downstream) -> {
                    for (Node node : cardLine) {
                        if (node instanceof Element element) {
                            var tagName = element.tagName();

                            if (tagName.equalsIgnoreCase("a") || tagName.equalsIgnoreCase("img")) {
                                String title = element.attr("title");
                                String link = element.attr("href");

                                if (StringUtils.isBlank(link)) {
                                    link = element.attr("src");
                                }
                                if (StringUtils.isNotBlank(link) || StringUtils.isNotBlank(title)) {
                                    downstream.accept(Pair.of(title, sanitizeLink(link)));
                                }
                            }
                        }
                    }
                })
                .distinct()
                .toList();
    }

    private static Stream<List<Node>> getCardLines(Element td) {
        return PCAUtils.split(td.childNodes(), node -> node instanceof Element element && element.tagName().equalsIgnoreCase("br"));
    }

    private String sanitizeLink(String link) {
        var value = StringUtils.removeEnd(link, ".png");

        value = StringUtils.removeEnd(value, ".jpg");
        value = StringUtils.removeEnd(value, ".gif");
        value = StringUtils.removeEnd(value, ".jpeg");
        value = StringUtils.removeEnd(value, ".svg");
        value = StringUtils.removeStart(value, "/");
        value = StringUtils.removeStart(value, "wiki/");
        value = StringUtils.removeStart(value, "Images/Mark/");
        value = StringUtils.removeStart(value, "File:");
        return value.trim();
    }
}
