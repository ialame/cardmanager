package com.pcagrade.retriever.card.yugioh.serie;

import com.github.f4b6a3.ulid.UlidCreator;
import com.pcagrade.retriever.annotation.RetrieverTest;
import com.pcagrade.retriever.card.yugioh.serie.translation.YuGiOhSerieTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RetrieverTest(YuGiOhSerieMapperTestConfig.class)
class YuGiOhSerieMapperShould {

    @Autowired
    private YuGiOhSerieMapper yuGiOhSerieMapper;

    @Test
    void update() {
        var dto = new YuGiOhSerieDTO();
        var us = new YuGiOhSerieTranslationDTO();

        us.setLocalization(Localization.USA);
        us.setName("Test");
        dto.setId(UlidCreator.getUlid());
        dto.setTranslations(Map.of(Localization.USA, us));

        var serie = new YuGiOhSerie();

        yuGiOhSerieMapper.update(serie, dto);

        assertThat(serie).usingRecursiveComparison()
                .ignoringFields("sets", "idPca", "translations")
                .isEqualTo(dto);

        assertThat(serie.getTranslation(Localization.USA)).usingRecursiveComparison()
                .ignoringFields("id", "serie", "translatable", "active")
                .isEqualTo(us);
    }
}
