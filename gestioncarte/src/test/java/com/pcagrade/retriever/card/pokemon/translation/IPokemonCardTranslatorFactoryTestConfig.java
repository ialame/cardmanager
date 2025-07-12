package com.pcagrade.retriever.card.pokemon.translation;

import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.retriever.card.pokemon.PokemonFieldMappingServiceTestConfig;
import com.pcagrade.retriever.card.pokemon.source.dictionary.DictionaryServiceTestConfig;
import com.pcagrade.retriever.card.pokemon.source.jcc.JCCPokemonTfServiceTestConfig;
import com.pcagrade.retriever.card.pokemon.source.official.OfficialSiteTestConfig;
import com.pcagrade.retriever.card.pokemon.source.official.jp.JapaneseOfficialSiteTestConfig;
import com.pcagrade.retriever.card.pokemon.source.pkmncards.PkmncardsComTestConfig;
import com.pcagrade.retriever.card.pokemon.source.pokecardex.PokecardexComTestConfig;
import com.pcagrade.retriever.card.pokemon.source.ptcgo.PtcgoTestConfig;
import com.pcagrade.retriever.card.pokemon.source.wiki.WikiTestConfig;
import org.springframework.context.annotation.Import;

@RetrieverTestConfiguration
@Import({ GenericNameParserTestConfig.class, PokemonFieldMappingServiceTestConfig.class, PtcgoTestConfig.class, DictionaryServiceTestConfig.class, WikiTestConfig.class, JapaneseOfficialSiteTestConfig.class, PkmncardsComTestConfig.class, OfficialSiteTestConfig.class, JCCPokemonTfServiceTestConfig.class, PokecardexComTestConfig.class })
public class IPokemonCardTranslatorFactoryTestConfig {

}
