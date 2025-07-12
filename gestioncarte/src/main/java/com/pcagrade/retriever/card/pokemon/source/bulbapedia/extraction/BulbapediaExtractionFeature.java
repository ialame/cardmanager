package com.pcagrade.retriever.card.pokemon.source.bulbapedia.extraction;

import java.io.Serializable;

public class BulbapediaExtractionFeature implements Serializable {

	private String title;
	private String imgHref;

	public BulbapediaExtractionFeature(String title, String imgHref) {
		this.title = title;
		this.imgHref = imgHref;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImgHref() {
		return imgHref;
	}

	public void setImgHref(String imgHref) {
		this.imgHref = imgHref;
	}

}
