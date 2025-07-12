package com.pcagrade.retriever.card.lorcana.source.mushu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.cache.CacheService;
import com.pcagrade.retriever.card.yugioh.YuGiOhFieldMappingService;
import com.pcagrade.retriever.parser.wiki.WikiParser;
import com.pcagrade.retriever.parser.wiki.WikiParserHelper;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sweble.wikitext.parser.nodes.WtTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class MushuParser {

    private static final String CARD = "Card";
    private static final String CARD_RELEASES = "CardReleases";

    private static final List<Localization> LOCALIZATIONS = List.of(Localization.USA, Localization.FRANCE, Localization.GERMANY, Localization.ITALY);

    private final WikiParser wikiParser;

    @Autowired
    private YuGiOhFieldMappingService fieldMappingService;

    public MushuParser(@Autowired ObjectMapper objectMapper, @Autowired CacheService cacheService) {
        this.wikiParser = new WikiParser(objectMapper, cacheService, "https://wiki.mushureport.com/api.php");
    }

    public List<MushuCard> getCards(String url) {
        return PCAUtils.fluxToList(wikiParser.ask("[[CardSet::" + url + "]]|sort=SetN|order=ascending|limit=250")
                .flatMapIterable(q -> q.results().keySet())
                .flatMap(this::getCardsFromPage));
    }

    private Flux<MushuCard> getCardsFromPage(String title) {
        return wikiParser.parsePage(title).flatMapMany(page -> {
            var cardTemplate = WikiParserHelper.findTemplate(page, CARD).orElse(null);

            if (cardTemplate == null) {
                return Mono.empty();
            }
            var ink = StringUtils.lowerCase(WikiParserHelper.getAsString(WikiParserHelper.getArgument(cardTemplate, "ink")));
            var illustrator = WikiParserHelper.getAsString(WikiParserHelper.getArgument(cardTemplate, "illustrator"));
            var releasesTemplates = WikiParserHelper.findTemplates(page, CARD_RELEASES);
            var first = releasesTemplates.isEmpty() ? null :releasesTemplates.get(0);

            return Flux.fromIterable(releasesTemplates).map(releaseTemplate -> {
                var number = WikiParserHelper.getArgumentAsString(releaseTemplate, 1);
                var set = WikiParserHelper.getArgumentAsString(releaseTemplate, 2);
                var rarity = WikiParserHelper.getArgumentAsString(releaseTemplate, 3);
                var translations = LOCALIZATIONS.stream()
                        .map(l -> {
                            var translation = l == Localization.USA ? getEnglishTranslation(title, number) : getTranslation(cardTemplate, l, number);

                            return translation != null ? Map.entry(l, translation) : null;
                        })
                        .filter(Objects::nonNull)
                        .collect(PCAUtils.toMap());

                return new MushuCard(translations, ink, set, rarity, illustrator, releaseTemplate != first);
            });
        });
    }

    private MushuCardTranslation getEnglishTranslation(String pageTitle, String number) {
        var split = pageTitle.split(" - ");
        var name = StringUtils.trimToEmpty(split[0]);
        var title = split.length > 1 ? StringUtils.trimToEmpty(split[1]) : "";

        return new MushuCardTranslation(name, title, number);
    }

    @Nullable
    private MushuCardTranslation getTranslation(WtTemplate template, Localization localization, String number) {
        var lower = localization.getCode().toLowerCase();
        var name = WikiParserHelper.getAsString(WikiParserHelper.getArgument(template, lower + "-name"));
        var title = WikiParserHelper.getAsString(WikiParserHelper.getArgument(template, lower + "-version"));
        var localizedNumber = StringUtils.replace(number, "EN", lower.toUpperCase());

        return StringUtils.isNotBlank(name) ? new MushuCardTranslation(name, title, localizedNumber) : null;
    }
}
