package com.pcagrade.retriever.card.onepiece.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.onepiece.set.translation.OnePieceSetTranslationDTO;
import com.pcagrade.mason.localization.Localization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class OnePieceSetServiceTest {

    public static final Ulid OP01_ID = Ulid.from("01GXGP22SM1HXDR9GJW9NQDCB4");
    private static final Ulid OP04_ID = Ulid.from("01H1RZ6HHYCG2MXBZSZE7TDAB0");

    @Autowired
    private OnePieceSetService onePieceSetService;
    @Test
    void getSets_shouldNot_beEmpty() {
        var all = onePieceSetService.getSets();

        assertThat(all).isNotEmpty();
    }

    @Test
    void save_should_addTranslation() {
        var set = onePieceSetService.findById(OP04_ID).orElseThrow();
        var us = new OnePieceSetTranslationDTO();

        us.setName("Kingdom Of Intrigue");
        us.setLocalization(Localization.USA);
        set.getTranslations().put(Localization.USA, us);

        var id = onePieceSetService.save(set);
        var savedSet = onePieceSetService.findById(id).orElseThrow();

        assertThat(savedSet).isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(set);
    }

}
