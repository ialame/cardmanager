package com.pcagrade.retriever.card.pokemon.feature.translation.pattern;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.mason.localization.Localization;
import com.pcagrade.retriever.card.pokemon.feature.FeatureRepository;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeatureTranslationPatternService {

    @Autowired
    private FeatureTranslationPatternRepository featureTranslationPatternRepository;

    @Autowired
    private FeatureTranslationPatternMapper featureTranslationPatternMapper;

    @Autowired
    private FeatureRepository featureRepository;

    public List<FeatureTranslationPatternDTO> getByFeatureId(Ulid featureId) {
       return featureTranslationPatternRepository.findAllByFeatureId(featureId).stream()
                .map(featureTranslationPatternMapper::mapToDTO)
                .toList();
    }

    public List<FeatureTranslationPatternDTO> getPatterns(Ulid featureId, String source, Localization localization) {
        return featureTranslationPatternRepository.findAllByFeatureIdAndSourceAndLocalization(featureId, source, localization).stream()
                .map(featureTranslationPatternMapper::mapToDTO)
                .toList();
    }

    @RevisionMessage("Sauvegarde des patterns de traduction de la particualrité {0}")
    public void setPatterns(@Nullable Ulid id, List<FeatureTranslationPatternDTO> patterns) {
        if (id == null) {
            return;
        }

        featureTranslationPatternRepository.findAllByFeatureId(id).stream()
                .filter(p -> patterns.stream().noneMatch(p2 -> p2.getId() == p.getId()))
                .forEach(featureTranslationPatternRepository::delete);
        featureRepository.findById(id).ifPresent(feature -> patterns.stream()
                .map(featureTranslationPatternMapper::mapFromDTO)
                .forEach(p -> {
                    var translation = feature.getTranslation(p.getLocalization());

                    if (translation != null) {
                        p.setFeature(feature);
                        p.setFeatureTranslation(translation);
                        featureTranslationPatternRepository.save(p);
                    }
                }));
    }
}
