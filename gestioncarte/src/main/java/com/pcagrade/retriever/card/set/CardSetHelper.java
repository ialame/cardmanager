package com.pcagrade.retriever.card.set;

import java.util.function.IntPredicate;

public class CardSetHelper {

    private CardSetHelper() { }

    public static int buildIdPca(Integer idPcaSerie, IntPredicate existsByIdPca) {
        for (int i = 1;; i++) {
            var pow = (int) Math.pow(10, (int) Math.floor(Math.log10(i) + 2));
            var idPca = (idPcaSerie != null ? idPcaSerie : 0) * pow + (i * 10);

            if (!existsByIdPca.test(idPca)) {
                return idPca;
            }
        }
    }
}
