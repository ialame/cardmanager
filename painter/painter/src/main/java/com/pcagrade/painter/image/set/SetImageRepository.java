package com.pcagrade.painter.image.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.repository.MasonRevisionRepository;
import com.pcagrade.mason.localization.Localization;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SetImageRepository extends MasonRevisionRepository<SetImage, Ulid> {

    List<SetImage> findAllBySetId(Ulid setId);
    Optional<SetImage> findFirstBySetIdAndLocalization(Ulid setId, Localization localization);

    void deleteAllBySetIdAndLocalization(Ulid setId, Localization localization);
}
