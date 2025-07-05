package com.pcagrade.retriever.localization.translation;

import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.MapKey;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import org.hibernate.envers.Audited;

import java.util.EnumMap;
import java.util.Map;

@Audited
@MappedSuperclass
public class AbstractTranslatableEntity<A, T extends AbstractTranslationEntity<A>> extends AbstractUlidEntity {

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "translatable", cascade = CascadeType.ALL)
    @MapKey(name = "localization")
    @Audited
    private Map<Localization, T> translations = new EnumMap<>(Localization.class);

    public Map<Localization, T> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<Localization, T> translations) {
        translations.forEach(this::setTranslation);
    }

    public void setTranslation(Localization localization, T translation) {
        if (translation == null) {
            translations.remove(localization);
            return;
        }
        translations.put(localization, translation);
        translation.setTranslatable((A) this);
        translation.setLocalization(localization);
    }

}
