package com.pcagrade.retriever.card.yugioh.source.official.pid;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRepository;
import com.pcagrade.mason.localization.Localization;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfficialSitePidRepository extends MasonRepository<OfficialSitePid, Integer> {

    List<OfficialSitePid> findAllBySetId(Ulid setId);

    @Query("select p.set.id from OfficialSitePid p where p.pid = ?1 and p.localization = ?2")
    Ulid findSetId(String pid, Localization localization);
}
