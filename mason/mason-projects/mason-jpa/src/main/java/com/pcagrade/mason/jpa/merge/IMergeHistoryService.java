package com.pcagrade.mason.jpa.merge;

import java.io.Serializable;
import java.util.List;

public interface IMergeHistoryService<I extends Serializable> {

    I getCurrentId(String table, I id);

    List<I> getMergedIds(String table, I id);

    void addMergeHistory(String table, I from, I to);

}
