package com.pcagrade.retriever.card.onepiece.set.extraction;

import com.pcagrade.retriever.card.onepiece.set.OnePieceSetDTO;
import com.pcagrade.retriever.card.onepiece.set.OnePieceSetService;
import com.pcagrade.retriever.card.onepiece.source.official.OnePieceOfficialSiteService;
import com.pcagrade.mason.localization.Localization;
import org.eclipse.jgit.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OnePieceSetExtractionService {

    @Autowired
    private OnePieceSetService onePieceSetService;
    @Autowired
    private OnePieceOfficialSiteService onePieceOfficialSiteService;

    public List<OnePieceSetDTO> unsavedSets() {
        var sets = onePieceSetService.getSets();

        return onePieceOfficialSiteService.getSets().stream()
                .<OnePieceSetDTO>mapMulti((s, downstream) -> sets.stream()
                        .filter(s2 -> StringUtils.equalsIgnoreCase(s.getCode(), s2.getCode()))
                        .findFirst()
                        .ifPresentOrElse(savedSet -> {
                            if (OnePieceSetDTO.CHANGES_COMPARATOR.compare(s, savedSet) != 0) {
                                s.setId(savedSet.getId());
                                downstream.accept(s);
                            }
                        }, () -> downstream.accept(s)))
                .toList();
    }

    public Optional<OnePieceSetDTO> findBySetCode(String code) {
        var opt = onePieceOfficialSiteService.findSetByCode(code);

        opt.ifPresent(s -> onePieceSetService.getSets().stream()
                .filter(s2 -> StringUtils.equalsIgnoreCase(s.getCode(), s2.getCode()))
                .findFirst()
                .ifPresent(s2 -> {
                    s.setId(s2.getId());

                    var us = s2.getTranslations().get(Localization.USA);

                    if (us != null && us.getReleaseDate() != null) {
                        s.getTranslations().values().forEach(t -> t.setReleaseDate(us.getReleaseDate()));
                    }
                }));
        return opt;
    }
}
