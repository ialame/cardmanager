package com.pcagrade.retriever.card.tag;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.tag.translation.CardTagTranslation;
import com.pcagrade.retriever.card.tag.type.CardTagType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames={"cardTags"})
public class CardTagService {

    @Autowired
    private CardTagRepository cardTagRepository;

    @Autowired
    private CardTagMapper cardTagMapper;

    @Cacheable
    public List<CardTagDTO> findAll() {
        return cardTagRepository.findAll().stream()
                .map(cardTagMapper::mapToDto)
                .toList();
    }

    @Cacheable
    public Optional<CardTagDTO> findById(Ulid id) {
        return cardTagRepository.findById(id)
                .map(cardTagMapper::mapToDto);
    }

    @CacheEvict(cacheNames = { "cardTags", "cardTagByName" }, allEntries = true)
    public Ulid save(CardTagDTO dto) {
        var tag = cardTagRepository.getOrCreate(dto.getId(), () -> cardTagMapper.mapFromDto(dto));
        var dtoTranslations = dto.getTranslations();
        var tagTranslations = tag.getTranslations();

        tag.setType(dto.getType());
        dtoTranslations.forEach((l, t) -> {
            if (StringUtils.isBlank(t.getName())) {
                return;
            }
            var translation = tag.getTranslations().get(l);

            if (translation == null) {
                translation = new CardTagTranslation();

                translation.setTranslatable(tag);
                translation.setLocalization(l);
                tag.getTranslations().put(l, translation);
            }
            translation.setName(t.getName());
        });
       tagTranslations.values().removeIf(t -> {
            var dtoTranslation = dtoTranslations.get(t.getLocalization());

            return dtoTranslation == null || StringUtils.isBlank(dtoTranslation.getName());
        });
       tagTranslations.forEach((l, t) -> {
            t.setTranslatable(tag);
            t.setLocalization(l);
        });
        return cardTagRepository.save(tag).getId();
    }

    @Cacheable("cardTagByName")
    public Optional<CardTagDTO> findByTypeAndName(CardTagType type, String name) {
        return cardTagRepository.findByTypeUsName(type, name)
                .map(cardTagMapper::mapToDto);
    }
}
