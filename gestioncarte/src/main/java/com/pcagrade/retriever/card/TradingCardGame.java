package com.pcagrade.retriever.card;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

@Schema
public enum TradingCardGame {

    POKEMON("pokemon", "pok"),
    MAGIC("magic", "mag"),
    YUGIOH("yugioh", "ygh"),
    ONE_PIECE("onepiece", "onp"),
    LORCANA("lorcana", "lor");

    private final String name;
    private final String code;

    TradingCardGame(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public static TradingCardGame getByName(String name) {
        for (TradingCardGame tcg : values()) {
            if (tcg.name.equals(name)) {
                return tcg;
            }
        }
        return null;
    }

    public static TradingCardGame getByCode(String code) {
        for (TradingCardGame tcg : values()) {
            if (tcg.code.equals(code)) {
                return tcg;
            }
        }
        return null;
    }

    @JsonCreator
    public static TradingCardGame parse(String tcg) {
        if (StringUtils.isBlank(tcg)) {
            return null;
        }

        tcg = tcg.toLowerCase();

        var tcgEnum = getByName(tcg);

        if (tcgEnum == null) {
            tcgEnum = getByCode(tcg);
        }
        return tcgEnum;
    }

}
