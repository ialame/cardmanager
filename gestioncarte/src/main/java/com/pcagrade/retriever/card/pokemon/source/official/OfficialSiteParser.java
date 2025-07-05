package com.pcagrade.retriever.card.pokemon.source.official;

import com.pcagrade.retriever.PCAUtils;
import com.pcagrade.retriever.card.pokemon.PokemonCardDTO;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetDTO;
import com.pcagrade.retriever.card.pokemon.source.official.path.OfficialSiteSetPathService;
import com.pcagrade.retriever.card.pokemon.translation.PokemonCardTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.parser.IHTMLParser;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class OfficialSiteParser {

	private static final Logger LOGGER = LogManager.getLogger(OfficialSiteParser.class);

	private static final Map<Localization, Pattern> ANGLE_PATTERNS = Map.of(
			Localization.USA, Pattern.compile("(top|bottom) (left|right)$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
			Localization.FRANCE, Pattern.compile("en (haut|bas) [aà] (gauche|droite)$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
			Localization.ITALY, Pattern.compile("in (alto|basso) a (sinistra|destra)$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
			Localization.GERMANY, Pattern.compile("(oben|unten) (links|rechts)$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
			Localization.SPAIN, Pattern.compile("(superior|inferior) (izquierda|derecha)$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
			Localization.PORTUGAL, Pattern.compile("\\(?(direita|esquerda)\\)? \\(?(superior|inferior)\\)?$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
	);
	private static final Pattern BRACKETS_PATTERN = Pattern.compile("\\[.*\\]$");

	@Autowired
	private IHTMLParser htmlParser;

	@Autowired
	private OfficialSiteSetPathService officialSiteSetPathService;

	@Value("${pokemon-com.url}")
	private String pokemonComUrl;

	public String getCardName(PokemonSetDTO set, PokemonCardDTO card, Localization localization) {
		return getUrlForCard(set, card).stream()
				.map(url -> getCardName(url, localization))
				.filter(StringUtils::isNotBlank)
				.findFirst()
				.orElse("");
	}

	public List<String> getUrl(PokemonSetDTO set, PokemonCardDTO card, Localization localization) {
		return getUrlForCard(set, card).stream()
				.map(url -> getUrlForLang(url, localization))
				.filter(StringUtils::isNotBlank)
				.map(url -> htmlParser.processUrl(this.pokemonComUrl, url))
				.toList();
	}


	public String getCardName(String url, Localization localization) {
		if (StringUtils.isBlank(url) || localization == null) {
			return "";
		}

		var newUrl = getUrlForLang(url, localization);
		var name = "";

		if (StringUtils.isBlank(newUrl)) {
			return "";
		}

		var pattern = ANGLE_PATTERNS.get(localization);

		name = getCardName(newUrl);
		if (pattern != null) {
			name = pattern.matcher(name).replaceAll("");
		}
		name = PCAUtils.clean(BRACKETS_PATTERN.matcher(name).replaceAll(""));
		LOGGER.trace("Card name for url {} is {}", newUrl, name);
		return name;
	}

	private List<String> getUrlForCard(PokemonSetDTO set, PokemonCardDTO card) {
		return officialSiteSetPathService.getPaths(set.getId()).stream()
				.filter(StringUtils::isNotBlank)
				.map(url -> {
					var number = parseNumber(card.getTranslations().get(Localization.USA));

					if (StringUtils.isNotBlank(number)) {
						return url + '/' + number + '/';
					}
					return "";
				})
				.filter(StringUtils::isNotBlank)
				.toList();
	}

	private String parseNumber(PokemonCardTranslationDTO translation) {
		if (translation == null) {
			return "";
		}

		var number = translation.getNumber();

		try {
			return String.valueOf(Integer.parseInt(number.split("/")[0].replace("(?![0-9])", "")));
		} catch (Exception e) {
			return number;
		}
	}

	private String getUrlForLang(String url, Localization localization) {
		try {
			return get(url).select("link[hreflang=" + getCode(localization) + "]").stream()
				.map(e -> e.attr("href"))
				.findAny()
				.orElse("");
		} catch (IOException e) {
			return "";
		}
	}

	private String getCode(Localization localization) {
		return switch (localization) {
			case USA -> "en";
			case PORTUGAL -> "pt-br";
			default -> localization.getCode();
		};
	}

	private String getCardName(String url) {
		try {
			return PCAUtils.clean(get(url).select("meta[name=pkm-title]").stream()
					.map(e -> e.attr("content"))
					.findAny()
					.orElse(""));
		} catch (IOException e) {
			return "";
		}
	}

	@Nonnull
	private Element get(String url) throws IOException {
		return htmlParser.get(htmlParser.processUrl(this.pokemonComUrl, url)).head();
	}
}
