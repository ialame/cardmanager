package com.pcagrade.retriever.card.pokemon.source.ptcgo;

import com.pcagrade.retriever.PCAException;

public class PtcgoException extends PCAException {
	
	private static final long serialVersionUID = 4L;

	public PtcgoException() {
		super();
	}

	public PtcgoException(String message) {
		super(message);
	}

	public PtcgoException(String message, Throwable cause) {
		super(message, cause);
	}

	public PtcgoException(Throwable cause) {
		super(cause);
	}

	protected PtcgoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
