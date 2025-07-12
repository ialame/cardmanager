package com.pcagrade.retriever.card.pokemon.source.bulbapedia;

import com.pcagrade.retriever.PCAException;

public class BulbapediaException extends PCAException {

	private static final long serialVersionUID = 4L;
	
	private final boolean handled;

	public BulbapediaException() {
		this(false);
	}

	public BulbapediaException(String message) {
		this(message, false);
	}

	public BulbapediaException(String message, Throwable cause) {
		this(message, cause, false);
	}

	public BulbapediaException(Throwable cause) {
		this(cause, false);
	}
	
	protected BulbapediaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		this(message, cause, enableSuppression, writableStackTrace, false);
	}
	
	public BulbapediaException(boolean handled) {
		super();
		this.handled = handled;
	}

	public BulbapediaException(String message, boolean handled) {
		super(message);
		this.handled = handled;
	}

	public BulbapediaException(String message, Throwable cause, boolean handled) {
		super(message, cause);
		this.handled = handled;
	}

	public BulbapediaException(Throwable cause, boolean handled) {
		super(cause);
		this.handled = handled;
	}
	
	
	protected BulbapediaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, boolean handled) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.handled = handled;
	}

	public boolean isHandled() {
		return handled;
	}
}
