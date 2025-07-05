package com.pcagrade.retriever.card.promo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.ILocalized;
import com.pcagrade.mason.localization.Localization;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Comparator;

@JsonSerialize(as = PromoCardDTO.class)
public class PromoCardDTO implements ILocalized {

	public static final Comparator<PromoCardDTO> CHANGES_COMPARATOR = Comparator.comparing(PromoCardDTO::getLocalization)
			.thenComparing(PromoCardDTO::getName, ObjectUtils::compare);

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Ulid id;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private Localization localization;

	private Ulid versionId;
	private Ulid eventId;
	private boolean used;

	public PromoCardDTO() {}

	public PromoCardDTO(String name, Localization localization) {
		this.name = name;
		this.localization = localization;
	}

	public Ulid getId() {
		return id;
	}

	public void setId(Ulid id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Localization getLocalization() {
		return localization;
	}

	@Override
	public void setLocalization(Localization localization) {
		this.localization = localization;
	}

	public Ulid getVersionId() {
		return versionId;
	}

	public void setVersionId(Ulid versionId) {
		this.versionId = versionId;
	}

	public Ulid getEventId() {
		return eventId;
	}

	public void setEventId(Ulid eventId) {
		this.eventId = eventId;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}
}
