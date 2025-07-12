package com.pcagrade.retriever.card.pokemon.source.wiki;

import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.source.wiki.url.WikiUrlDTO;
import com.pcagrade.retriever.parser.IHTMLParser;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@CacheConfig(cacheNames = "WikiPokemonCentralItParser")
public class WikiPokemonCentralItParser extends AbstractWikiParser {

	private static final Logger LOGGER = LogManager.getLogger(WikiPokemonCentralItParser.class);

	@Autowired
	private IHTMLParser htmlParser;


	@Value("${wiki-pokemoncentral-it.url}")
	private String wikiPokemonCentralItUrl;

	public WikiPokemonCentralItParser() {
		super("wiki-it-pokemoncentral", Localization.ITALY);
	}

	@Override
	protected List<Element> listTrs(String url, @Nonnull List<WikiUrlDTO> wikiUrls) {
		return wikiUrls.stream()
				.<Element>mapMulti((wikiUrl, downstream) -> {
					try {
						Elements elements = new Elements(get(url));
						var cssSelector = wikiUrl != null ? wikiUrl.cssSelector() : "";

						if (StringUtils.isNotBlank(cssSelector)) {
							elements = elements.select(cssSelector);
						} else {
							elements.select("table.roundy.value-center.white-rows.roundy-corners");
						}
						elements.select("tbody").select("tr").forEach(downstream);
					} catch (IOException e) {
						LOGGER.error("Error while parsing {}", url, e);
					}
				}).toList();
	}

	@Override
	public String getUrl(PokemonSetDTO set, @Nonnull List<WikiUrlDTO> wikiUrls) {
		for (var wikiUrl : wikiUrls) {
			var url = wikiUrl.url();

			if (StringUtils.isNotBlank(url)) {
				return url;
			}
			var translation = set.getTranslations().get(Localization.ITALY);

			if (translation != null) {
				return translation.getName() + "_(TCG)";
			}
		}
		return "";
	}

	@Nonnull
	private Element get(String url) throws IOException {
		return htmlParser.get(htmlParser.processUrl(this.wikiPokemonCentralItUrl, URLDecoder.decode(url, StandardCharsets.UTF_8))).body();
	}
}
