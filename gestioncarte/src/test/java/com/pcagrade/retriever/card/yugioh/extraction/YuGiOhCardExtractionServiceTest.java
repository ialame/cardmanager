package com.pcagrade.retriever.card.yugioh.extraction;

import com.github.f4b6a3.ulid.Ulid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class YuGiOhCardExtractionServiceTest {

    private static final Ulid MP22_ID = Ulid.from("01HK4QEGK7JXKZSGCWQZ902QZA");

    @Autowired
    private YuGiOhCardExtractionService yuGiOhCardExtractionService;

    void extractYuGiOhCards_should_extractMP22() {
        var list = yuGiOhCardExtractionService.extractYuGiOhCards(MP22_ID);

        assertThat(list).isNotEmpty().hasSize(277);
    }
}
