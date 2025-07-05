package com.pcagrade.retriever.card.onepiece;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.onepiece.set.OnePieceSetServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class OnePieceCardServiceTest {

    public static final Ulid OP01_001_ID = Ulid.from("01GXGPC560410PARKG79ZRA6G6");

    @Autowired
    private OnePieceCardService onePieceCardService;

    @Test
    void rebuildIdsPrim_should_setIdPrim() {
        onePieceCardService.rebuildIdsPrim(OnePieceSetServiceTest.OP01_ID);

        assertThat(onePieceCardService.findById(OP01_001_ID)).isNotEmpty().hasValueSatisfying(c -> assertThat(c.getIdPrim()).isNotNull());
    }
}
