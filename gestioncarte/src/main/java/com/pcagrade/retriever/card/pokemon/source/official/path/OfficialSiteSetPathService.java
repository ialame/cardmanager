package com.pcagrade.retriever.card.pokemon.source.official.path;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.pokemon.set.PokemonSet;
import com.pcagrade.retriever.card.pokemon.set.PokemonSetRepository;
import com.pcagrade.retriever.data.AbstractListDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OfficialSiteSetPathService {

    @Autowired
    private OfficialSiteSetPathRepository officialSiteSetPathRepository;
    @Autowired
    private PokemonSetRepository pokemonSetRepository;

    private final PathManager pathManager = new PathManager();

    public List<String> getPaths(Ulid setId) {
        return pathManager.getList(setId);
    }

    public void setPaths(Ulid setId, List<String> paths) {
        pathManager.setList(setId, paths);
    }

    private class PathManager extends AbstractListDataManager<OfficialSiteSetPath, String, PokemonSet, Ulid> {

        @Override
        protected List<OfficialSiteSetPath> getByTargetId(Ulid targetId) {
            return officialSiteSetPathRepository.findAllBySetId(targetId);
        }

        @Override
        protected String mapToDTO(OfficialSiteSetPath entity) {
            return entity.getPath() ;
        }

        @Override
        protected OfficialSiteSetPath mapToEntity(String dto, PokemonSet target) {
            var path = new OfficialSiteSetPath();

            path.setSet(target);
            path.setPath(dto);
            return path;
        }

        @Override
        protected Optional<PokemonSet> findTargetById(Ulid targetId) {
            return pokemonSetRepository.findById(targetId);
        }

        @Override
        protected void deleteAll(List<OfficialSiteSetPath> entities) {
            officialSiteSetPathRepository.deleteAll(entities);
        }

        @Override
        protected void save(OfficialSiteSetPath entity) {
            officialSiteSetPathRepository.save(entity);
        }
    }
}
