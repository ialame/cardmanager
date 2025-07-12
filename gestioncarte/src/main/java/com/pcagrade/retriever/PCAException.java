package com.pcagrade.retriever;

public class PCAException extends RuntimeException {

	private static final long serialVersionUID = 2L;

	public PCAException() {
		super();
	}

	public PCAException(String message) {
		super(message);
	}

	public PCAException(String message, Throwable cause) {
		super(message, cause);
	}

	public PCAException(Throwable cause) {
		super(cause);
	}

	protected PCAException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
