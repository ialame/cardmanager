package com.pcagrade.retriever.parser.wiki;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.cache.CacheService;
import com.pcagrade.retriever.parser.wiki.result.ask.WikiAskConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;


@RetrieverTest(WikiParserShould.Config.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WikiParserShould {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CacheService cacheService;

    @Value("${retriever.web-client.max-in-memory-size}")
    String maxInMemorySize;
    private WikiParser bulbapediaParser;
    private WikiParser mushuParser;

    @BeforeAll
    public void setUp() {
        this.bulbapediaParser = new WikiParser(objectMapper, cacheService, "https://bulbapedia.bulbagarden.net/w/api.php");
        this.mushuParser = new WikiParser(objectMapper, cacheService, "https://wiki.mushureport.com/api.php");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://bulbapedia.bulbagarden.net/wiki/Collection_Sun_(TCG)",
            "Collection_Sun_(TCG)",
            "Unnumbered_Promotional_cards_(TCG)",
            "Unnumbered_Promotional_cards_(TCG)/1996-2005",
            "Zeraora_VSTAR_%26_VMAX_High-Class_Deck_(TCG)",
            "Deoxys_VSTAR_%26_VMAX_High-Class_Deck_(TCG)",
            "XY_(TCG)",
            "Sun_%26_Moon_(TCG)",
            "SWSH_Black_Star_Promos_(TCG)",
            "SM_Black_Star_Promos_(TCG)",
            "SM-P_Promotional_cards_(TCG)",
            "S-P_Promotional_cards_(TCG)",
            "Beginning_Set_(TCG)",
            "Scarlet_%26_Violet_(TCG)",
            "EX_Team_Magma_vs_Team_Aqua_(TCG)",
            "Secret_Wonders_(TCG)",
            "Venusaur-EX_(XY_1)",
            "Beedrill_%CE%B4_(EX_Delta_Species_1)",
            "Arcanine_ex_(Violet_ex_16)",
            "Blaine%27s_Vulpix_(Gym_Challenge_66)"
    })
    void parseBulbapediaPage(String page) {
        var value = this.bulbapediaParser.parsePage(page).block();

        assertThat(value).isNotNull().isNotEmpty();
    }

    @Test
    void askMushuQuery() {
        var value = this.mushuParser.ask("[[CardSet::3]]|?Name|?Ink|?NameFR|?NameDE|?NameIT|?SetN|?Illustrator|?Rarity|sort=SetN|order=ascending|limit=250").block();

        assertThat(value).isNotNull().satisfies(v -> assertThat(v.results()).isNotEmpty());
    }

    @RetrieverTestConfiguration
    @Import(WikiAskConfiguration.class)
    static class Config { }

}
