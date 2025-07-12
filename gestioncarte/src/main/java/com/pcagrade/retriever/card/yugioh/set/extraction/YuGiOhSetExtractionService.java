package com.pcagrade.retriever.card.yugioh.set.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.ulid.UlidCreator;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetDTO;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetService;
import com.pcagrade.retriever.extraction.AbstractExtractionService;
import com.pcagrade.retriever.extraction.consolidation.ConsolidationService;
import com.pcagrade.retriever.extraction.consolidation.source.IConsolidationSource;
import com.pcagrade.retriever.extraction.consolidation.source.INamedConsolidationSource;
import com.pcagrade.retriever.extraction.consolidation.source.SimpleConsolidationSource;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class YuGiOhSetExtractionService extends AbstractExtractionService<ExtractedYuGiOhSetDTO> {

    private static final Logger LOGGER = LogManager.getLogger(YuGiOhSetExtractionService.class);

    public static final String NAME = "yugioh_sets";

    @Autowired
    private List<IYuGiOhSetSourceService> yuGiOhSetSourceServices;
    @Autowired
    private YuGiOhSetService yuGiOhSetService;
    @Autowired
    private ConsolidationService consolidationService;

    public YuGiOhSetExtractionService(ObjectMapper mapper) {
        super(NAME, mapper, ExtractedYuGiOhSetDTO.class);
    }

    public void extract() {
        this.putSets(doExtract());
    }

    private List<ExtractedYuGiOhSetDTO> doExtract() {
        if (CollectionUtils.isEmpty(yuGiOhSetSourceServices)) {
            return Collections.emptyList();
        }

        return createConsolidationGroups(yuGiOhSetSourceServices.parallelStream()
                .map(YuGiOhSetExtractionService::safeExtractSets)
                .toList(), this::shouldGroupSets).stream()
                .map(this::createExtractedSet)
                .filter(Objects::nonNull)
                .filter(this::hasChanges)
                .toList();
    }

    private boolean shouldGroupSets(YuGiOhSetDTO set1, YuGiOhSetDTO set2) {
        var localizations = Localization.Group.YUGIOH.getLocalizations();

        for (var localization : localizations) {
            var t1 = set1.getTranslations().get(localization);
            var t2 = set2.getTranslations().get(localization);

            if (t1 != null && t2 != null && (StringUtils.equals(getComparisonName(t1.getName()), getComparisonName(t2.getName())) || (StringUtils.isNotBlank(t1.getCode()) && StringUtils.isNotBlank(t2.getCode()) && StringUtils.equalsIgnoreCase(t1.getCode(), t2.getCode())))) {
                return true;
            }
        }

        var pids1 = set1.getOfficialSitePids();
        var pids2 = set2.getOfficialSitePids();

        return CollectionUtils.isNotEmpty(pids1) && CollectionUtils.isNotEmpty(pids2) && pids1.stream().anyMatch(pids2::contains);
    }

    // we do it manually to get better performance
    private String getComparisonName(String name) {
        if (StringUtils.isBlank(name)) {
            return "";
        }

        var builder = new StringBuilder();

        for (var c : name.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                builder.append(Character.toLowerCase(c));
            }
        }
        return builder.toString();
    }

    private static List<INamedConsolidationSource<YuGiOhSetDTO>> safeExtractSets(IYuGiOhSetSourceService source) {
        try {
            return source.getAllSets();
        } catch (Exception e) {
            LOGGER.error(() -> "Error while extracting sets from source " + source, e);
            return Collections.emptyList();
        }
    }


    @Nullable
    private ExtractedYuGiOhSetDTO createExtractedSet(List<? extends IConsolidationSource<YuGiOhSetDTO>> sources) {
        var set = consolidationService.consolidate(YuGiOhSetDTO.class, sources);

        if (set == null) {
            return null;
        }

        var extractedSet = new ExtractedYuGiOhSetDTO();

        extractedSet.setId(UlidCreator.getUlid());
        extractedSet.setSet(set);
        extractedSet.setSavedSets(yuGiOhSetService.finSavedSets(set));
        extractedSet.setSources(sources.stream()
                .map(SimpleConsolidationSource::from)
                .toList());
        return extractedSet;
    }

    private boolean hasChanges(ExtractedYuGiOhSetDTO extractedSet) {
        var set = extractedSet.getSet();

        return extractedSet.getSavedSets().stream().allMatch(savedSet -> YuGiOhSetDTO.CHANGES_COMPARATOR.compare(set, savedSet) != 0);
    }

    public void putSets(List<ExtractedYuGiOhSetDTO> list) {
        this.put(list, (e1, e2) -> YuGiOhSetDTO.CHANGES_COMPARATOR.compare(e1.getSet(), e2.getSet()));
    }
}
