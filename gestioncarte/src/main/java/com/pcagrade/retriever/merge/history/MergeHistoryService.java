package com.pcagrade.retriever.merge.history;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.merge.IMergeHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MergeHistoryService implements IMergeHistoryService<Ulid> {

    @Autowired
    private MergeHistoryRepository mergeHistoryRepository;


    @Override
    public Ulid getCurrentId(String table, Ulid id) {
        return mergeHistoryRepository.findByTableAndFrom(table, id)
                .map(m -> getCurrentId(table, m.getTo()))
                .orElse(id);
    }

    @Override
    public List<Ulid> getMergedIds(String table, Ulid id) {
        return mergeHistoryRepository.findAllByTableAndTo(table, id).stream()
                .map(MergeHistory::getFrom)
                .toList();
    }

    @Override
    public void addMergeHistory(String table, Ulid from, Ulid to) {
        var mergeHistory = new MergeHistory();
        int last = mergeHistoryRepository.getLastRevision();

        if (last > 0) {
            mergeHistory.setFrom(from);
            mergeHistory.setTo(to);
            mergeHistory.setRevision(last);
            mergeHistory.setTable(table);
            mergeHistoryRepository.save(mergeHistory);
        }
    }


}
