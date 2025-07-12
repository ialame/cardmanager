package com.pcagrade.retriever.card.pokemon.feature.translation.pattern;

import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.localization.jpa.LocalizationAttributeConverter;
import com.pcagrade.mason.localization.jpa.LocalizationColumnDefinitions;
import com.pcagrade.retriever.card.pokemon.feature.Feature;
import com.pcagrade.retriever.card.pokemon.feature.translation.FeatureTranslation;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.envers.Audited;

import java.util.Comparator;

@Entity
@Table(name = "j_feature_translation_pattern")
@Audited
public class FeatureTranslationPattern implements ILocalized {

	public static final Comparator<FeatureTranslationPattern> LENGTH_COMPARATOR = Comparator.comparing(f -> f.getTitle().length());

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "locale", columnDefinition = LocalizationColumnDefinitions.NON_NULL)
	@Convert(converter = LocalizationAttributeConverter.class)
	private Localization localization;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "feature_id")
	private Feature feature;

	@ManyToOne
	@JoinColumn(name = "feature_translation_id")
	private FeatureTranslation featureTranslation;

	@Column(name =  "replacement_name")
	private String replacementName;
	@Column(name =  "replacement_label_name")
	private String replacementLabelName;

	@Column
	private String source;

	@Column(name = "img_href")
	private String imgHref;

	private String title;

	private String regex;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public Localization getLocalization() {
		return localization;
	}

	@Override
	public void setLocalization(Localization localization) {
		this.localization = localization;
	}

	public Feature getFeature() {
		return feature;
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public FeatureTranslation getFeatureTranslation() {
		return featureTranslation;
	}

	public void setFeatureTranslation(FeatureTranslation featureTranslation) {
		this.featureTranslation = featureTranslation;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getImgHref() {
		return imgHref;
	}

	public void setImgHref(String imgHref) {
		this.imgHref = imgHref;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getReplacementName() {
		return replacementName;
	}

	public void setReplacementName(String replacementName) {
		this.replacementName = replacementName;
	}

	public String getReplacementLabelName() {
		return replacementLabelName;
	}

	public void setReplacementLabelName(String replacementLabelName) {
		this.replacementLabelName = replacementLabelName;
	}
}
