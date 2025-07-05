package com.pcagrade.retriever.card.pokemon.source.wiki.url;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.set.PokemonSet;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetRepository;
import com.pcagrade.retriever.data.AbstractListDataManager;
import com.pcagrade.mason.localization.Localization;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@CacheConfig(cacheNames = { "wikiUrl" })
public class WikiUrlService {

    @Autowired
    private WikiUrlRepository wikiUrlRepository;
    @Autowired
    private PokemonSetRepository pokemonSetRepository;
    @Autowired
    private WikiUrlMapper wikiUrlMapper;

    private final UrlManager urlManager = new UrlManager();

    @Cacheable
    public List<WikiUrlDTO> getUrls(@Nonnull Ulid setId, @Nonnull Localization localization) {
        return urlManager.getList(Pair.of(setId, localization));
    }

    @CacheEvict(allEntries = true)
    public void setUrls(@Nonnull Ulid setId, @Nonnull Localization localization, @Nonnull List<WikiUrlDTO> urls) {
        urlManager.setList(Pair.of(setId, localization), urls);
    }

    private class UrlManager extends AbstractListDataManager<WikiUrl, WikiUrlDTO, Pair<PokemonSet, Localization>, Pair<Ulid, Localization>> {

        @Override
        protected List<WikiUrl> getByTargetId(Pair<Ulid, Localization> targetId) {
            return wikiUrlRepository.findAllBySetIdAndLocalization(targetId.getLeft(), targetId.getRight());
        }

        @Override
        protected WikiUrlDTO mapToDTO(WikiUrl entity) {
            return wikiUrlMapper.mapToDto(entity);
        }

        @Override
        protected WikiUrl mapToEntity(WikiUrlDTO dto, Pair<PokemonSet, Localization> target) {
            var url = new WikiUrl();

            url.setSet(target.getLeft());
            url.setLocalization(target.getRight());
            wikiUrlMapper.update(url, dto);
            return url;
        }

        @Override
        protected Optional<Pair<PokemonSet, Localization>> findTargetById(Pair<Ulid, Localization> targetId) {
            var set = pokemonSetRepository.findById(targetId.getLeft());

            return set.map(pokemonSet -> Pair.of(pokemonSet, targetId.getRight()));
        }

        @Override
        protected void deleteAll(List<WikiUrl> entities) {
            wikiUrlRepository.deleteAll(entities);
        }

        @Override
        protected void save(WikiUrl entity) {
            wikiUrlRepository.save(entity);
        }
    }

}
