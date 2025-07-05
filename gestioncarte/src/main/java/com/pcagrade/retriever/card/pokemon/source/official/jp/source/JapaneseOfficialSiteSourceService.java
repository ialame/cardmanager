package com.pcagrade.retriever.card.pokemon.source.official.jp.source;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.set.PokemonSet;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetRepository;
import com.pcagrade.retriever.data.AbstractListDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JapaneseOfficialSiteSourceService {

    @Autowired
    private JapaneseOfficialSiteSourceRepository japaneseOfficialSiteSourceRepository;
    @Autowired
    private PokemonSetRepository pokemonSetRepository;

    private final PgManager pgManager = new PgManager();

    public List<String> getPgs(Ulid setId) {
        return pgManager.getList(setId);
    }

    public void setPgs(Ulid setId, List<String> pgs) {
        pgManager.setList(setId, pgs);
    }

    private class PgManager extends AbstractListDataManager<JapaneseOfficialSiteSource, String, PokemonSet, Ulid> {

        @Override
        protected List<JapaneseOfficialSiteSource> getByTargetId(Ulid targetId) {
            return japaneseOfficialSiteSourceRepository.findAllBySetId(targetId);
        }

        @Override
        protected String mapToDTO(JapaneseOfficialSiteSource entity) {
            return entity.getPg();
        }

        @Override
        protected JapaneseOfficialSiteSource mapToEntity(String dto, PokemonSet target) {
            var pg = new JapaneseOfficialSiteSource();

            pg.setSet(target);
            pg.setPg(dto);
            return pg;
        }

        @Override
        protected Optional<PokemonSet> findTargetById(Ulid targetId) {
            return pokemonSetRepository.findById(targetId);
        }

        @Override
        protected void deleteAll(List<JapaneseOfficialSiteSource> entities) {
            japaneseOfficialSiteSourceRepository.deleteAll(entities);
        }

        @Override
        protected void save(JapaneseOfficialSiteSource entity) {
            japaneseOfficialSiteSourceRepository.save(entity);
        }
    }
}
