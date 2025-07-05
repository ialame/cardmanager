package com.pcagrade.retriever.merge.history;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.TestUlidProvider;

import java.util.List;

public class MergeHistoryTestProvider {

    public static final List<MergeHistory> HISTORY = List.of(
            create("pokemon_card", TestUlidProvider.ID_2, TestUlidProvider.ID_1),
            create("pokemon_card", TestUlidProvider.ID_3, TestUlidProvider.ID_1)
    );

    public static MergeHistory create(String table, Ulid form, Ulid to) {
        var mergeHistory = new MergeHistory();

        mergeHistory.setTable(table);
        mergeHistory.setFrom(form);
        mergeHistory.setTo(to);
        mergeHistory.setRevision(0);
        return mergeHistory;
    }

}
