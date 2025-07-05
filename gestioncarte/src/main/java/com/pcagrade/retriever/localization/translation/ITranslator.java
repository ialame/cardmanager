package com.pcagrade.retriever.localization.translation;

import com.pcagrade.mason.localization.Localization;

import java.util.Optional;

public interface ITranslator<T> {

	Optional<T> translate(T source, Localization localization);
	
	String getName();
}
