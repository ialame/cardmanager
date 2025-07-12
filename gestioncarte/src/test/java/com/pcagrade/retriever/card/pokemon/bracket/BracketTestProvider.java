package com.pcagrade.retriever.card.pokemon.bracket;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.localization.Localization;

import java.util.List;

public class BracketTestProvider {

	public static final Ulid STAFF_ID = Ulid.from("01G4GEYY0YBBX5KX3Z42SM175T");
	public static final Ulid XY_SUPER_HOT_START_UP_LOGO_ID = Ulid.from("01G4GEQXF4NZWEQHMMMDEZ3V2H");

	public static List<Bracket> provideValidBrackets() {
	    return List.of(normal(), attack(), speed());
	}

	public static Bracket normal() {
		var bracket = new Bracket();

		bracket.setId(Ulid.from("01G4GEYY0YBBX5KX3Z42SM175F"));
		bracket.setLocalization(Localization.USA);
		bracket.setName("Normal");
		return bracket;
	}

	public static Bracket attack() {
		var bracket = new Bracket();

		bracket.setId(Ulid.from("01G4GEYY0YBBX5KX3Z42SM175G"));
		bracket.setLocalization(Localization.USA);
		bracket.setName("Attack");
		return bracket;
	}

	public static Bracket speed() {
		var bracket = new Bracket();

		bracket.setId(Ulid.from("01G4GEYY0YBBX5KX3Z42SM175J"));
		bracket.setLocalization(Localization.USA);
		bracket.setName("Speed");
		return bracket;
	}

	public static Bracket staff() {
		var bracket = new Bracket();

		bracket.setId(STAFF_ID);
		bracket.setLocalization(Localization.USA);
		bracket.setName("Staff");
		return bracket;
	}

	public static BracketDTO staffDTO() {
		var bracket = new BracketDTO();

		bracket.setId(STAFF_ID);
		bracket.setLocalization(Localization.USA);
		bracket.setName("Staff");
		return bracket;
	}

	public static BracketDTO xySuperHotStartUpLogoDTO() {
		var bracket = new BracketDTO();

		bracket.setId(XY_SUPER_HOT_START_UP_LOGO_ID);
		bracket.setLocalization(Localization.JAPAN);
		bracket.setName("XY Super Hot Start-Up logo");
		return bracket;
	}
}
