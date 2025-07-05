package com.pcagrade.retriever.card.pokemon.source.jcc;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.cache.IEvictable;
import com.pcagrade.retriever.card.pokemon.serie.PokemonSerieService;
import com.pcagrade.retriever.card.pokemon.serie.translation.PokemonSerieTranslationDTO;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.translation.GenericNameParser;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.parser.IHTMLParser;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@CacheConfig(cacheNames = "jccParser")
public class JCCPokemonTfParser implements IEvictable, IJCCPokemonTfParser {

	@Autowired
	private IHTMLParser htmlParser;

	@Autowired
	private PokemonSerieService pokemonSerieService;

	@Autowired
	private GenericNameParser genericNameParser;

	@Value("${jcc-pokemon-tf.url}")
	private String jccUrl;

	private final Map<Ulid, Integer> pageCounts = new HashMap<>();

	@Override
	@Cacheable
	public Map<String, Pair<String, String>> parse(PokemonSetDTO set) {
		String totalNumber = set.getTotalNumber();

		return listTrs(set).stream()
				.<Pair<String, Pair<String, String>>>mapMulti((tr, downstream) -> {
					var children = tr.children();

					if (children.size() >= 3) {
						var pair = genericNameParser.parseName(children.get(0), "jcc-pokemon-tf", Localization.FRANCE);

						if (pair != null) {
							downstream.accept(Pair.of(children.get(2).text() + "/" + totalNumber, pair));
						}
					}
				}).collect(PCAUtils.toMap());
	}

	@Override
	public String getUrl(PokemonSetDTO set) {
		var baseUrl = getBaseUrl(set);

		if (StringUtils.isNotBlank(baseUrl)) {
			return htmlParser.processUrl(this.jccUrl, baseUrl);
		}
		return "";
	}

	private String getBaseUrl(PokemonSetDTO set) {
		var translation = set.getTranslations().get(Localization.FRANCE);

		if (translation != null) {
			PokemonSerieTranslationDTO serieTranslation = pokemonSerieService.findById(set.getSerieId())
					.map(serie -> serie.getTranslations().get(Localization.FRANCE))
					.orElse(null);

			if (serieTranslation != null) {
				return "Bloc-" + mapName(serieTranslation.getName()) +  "/" + mapName(translation.getName()) + "/Liste-des-Cartes/fr/";
			}
		}
		return "";
	}

	private List<Element> listTrs(PokemonSetDTO set) {
		var baseUrl = getBaseUrl(set);

		if (StringUtils.isBlank(baseUrl)) {
			return Collections.emptyList();
		}
		baseUrl += "Page-";

		var id = set.getId();
		List<Element> trs = new ArrayList<>(200);
		int max = getPageCount(id);
		int i = 0;

		try {
			for (; i < max; i++) {
				var extracted = get(baseUrl + i + '/').select("tbody").select("tr");

				if(CollectionUtils.isEmpty(extracted)) {
					setPageCount(id, i);
					return trs;
				} else {
					trs.addAll(extracted);
				}
			}
		} catch (IOException e) {
			setPageCount(id, i);
		}
		return trs;
	}

	private int getPageCount(Ulid id) {
		synchronized (pageCounts) {
			return pageCounts.getOrDefault(id, 20);
		}
	}

	private void setPageCount(Ulid id, int value) {
		synchronized (pageCounts) {
			pageCounts.put(id, value);
		}
	}

	@Nonnull
	private Element get(String url) throws IOException {
		return htmlParser.get(htmlParser.processUrl(this.jccUrl, url)).body();
	}

	private String mapName(String name) {
		return Normalizer.normalize(name, Normalizer.Form.NFKD).replaceAll("\\p{M}", "").replaceAll("\\s", "-").replace("&", "et");
	}

	@Override
	public void evict() {
		synchronized (pageCounts) {
			pageCounts.clear();
		}
	}

}
