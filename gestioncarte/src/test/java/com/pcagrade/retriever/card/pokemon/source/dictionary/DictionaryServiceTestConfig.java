package com.pcagrade.retriever.card.pokemon.source.dictionary;


import com.pcagrade.retriever.RetrieverTestUtils;
import com.pcagrade.retriever.annotation.RetrieverTestConfiguration;
import com.pcagrade.mason.localization.Localization;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@RetrieverTestConfiguration
public class DictionaryServiceTestConfig {

	private DictionaryEntry venusaurFrance() {
		var entry = new DictionaryEntry();

		entry.setCount(1);
		entry.setFrom(Localization.USA);
		entry.setFromValue("Venusaur");
		entry.setTo(Localization.FRANCE);
		entry.setToValue("Florizarre");
		return entry;
	}

	private DictionaryEntry venusaurGermany() {
		var entry = new DictionaryEntry();

		entry.setCount(1);
		entry.setFrom(Localization.USA);
		entry.setFromValue("Venusaur");
		entry.setTo(Localization.GERMANY);
		entry.setToValue("Bisaflor");
		return entry;
	}

	private DictionaryEntry venusaurSpain() {
		var entry = new DictionaryEntry();

		entry.setCount(1);
		entry.setFrom(Localization.USA);
		entry.setFromValue("Venusaur");
		entry.setTo(Localization.SPAIN);
		entry.setToValue("Venusaur");
		return entry;
	}

	private DictionaryEntry venusaurEXFrance() {
		var entry = venusaurFrance();

		entry.setFromValue("Venusaur EX");
		entry.setToValue("Florizarre EX");
		return entry;
	}

	private DictionaryEntry venusaurEXGermany() {
		var entry = venusaurGermany();

		entry.setFromValue("Venusaur EX");
		entry.setToValue("Bisaflor EX");
		return entry;
	}

	private DictionaryEntry venusaurEXSpain() {
		var entry = venusaurSpain();

		entry.setFromValue("Venusaur EX");
		entry.setToValue("Venusaur EX");
		return entry;
	}

	@Bean
	public DictionaryService dictionaryService() {
		return new DictionaryService();
	}

	@Bean
	public DictionaryEntryRepository dictionaryEntryRepository() {
		var bean = RetrieverTestUtils.mockRepository(DictionaryEntryRepository.class);

		Mockito.when(bean.findFirstByFromAndToAndFromValueIgnoreCaseOrderByCountDesc(Localization.USA, Localization.FRANCE, "Venusaur")).thenReturn(Optional.of(venusaurFrance()));
		Mockito.when(bean.findFirstByFromAndToAndFromValueIgnoreCaseOrderByCountDesc(Localization.USA, Localization.FRANCE, "Venusaur EX")).thenReturn(Optional.of(venusaurEXFrance()));
		Mockito.when(bean.findFirstByFromAndToAndFromValueIgnoreCaseOrderByCountDesc(Localization.USA, Localization.GERMANY, "Venusaur")).thenReturn(Optional.of(venusaurGermany()));
		Mockito.when(bean.findFirstByFromAndToAndFromValueIgnoreCaseOrderByCountDesc(Localization.USA, Localization.GERMANY, "Venusaur EX")).thenReturn(Optional.of(venusaurEXGermany()));
		Mockito.when(bean.findFirstByFromAndToAndFromValueIgnoreCaseOrderByCountDesc(Localization.USA, Localization.SPAIN, "Venusaur")).thenReturn(Optional.of(venusaurSpain()));
		Mockito.when(bean.findFirstByFromAndToAndFromValueIgnoreCaseOrderByCountDesc(Localization.USA, Localization.SPAIN, "Venusaur EX")).thenReturn(Optional.of(venusaurEXSpain()));
		return bean;
	}

}
