package com.pcagrade.retriever.card.lorcana.source.mushu.set;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.retriever.card.lorcana.set.LorcanaSet;
import com.pcagrade.retriever.card.lorcana.set.LorcanaSetRepository;
import com.pcagrade.retriever.data.AbstractListDataManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MushuSetService {

    @Autowired
    private MushuSetRepository mushuSetRepository;
    @Autowired
    private LorcanaSetRepository lorcanaSetRepository;

    private final PidManager mushuSetManager = new PidManager();

    public List<MushuSetDTO> getMushuSets(Ulid setId) {
        return mushuSetManager.getList(setId);
    }

    @RevisionMessage("Modification des line mushu pour l''extension {0}")
    public void setMushuSets(Ulid setId, List<MushuSetDTO> sets) {
        mushuSetManager.setList(setId, sets);
    }

    private class PidManager extends AbstractListDataManager<MushuSet, MushuSetDTO, LorcanaSet, Ulid> {

        @Override
        protected List<MushuSet> getByTargetId(Ulid targetId) {
            return mushuSetRepository.findAllBySetId(targetId);
        }

        @Override
        protected MushuSetDTO mapToDTO(MushuSet entity) {
            return new MushuSetDTO(StringUtils.trimToEmpty(entity.getKey()), StringUtils.trimToEmpty(entity.getName()));
        }

        @Override
        protected MushuSet mapToEntity(MushuSetDTO dto, LorcanaSet target) {
            var set = new MushuSet();

            set.setKey(dto.key());
            set.setName(dto.name());
            set.setSet(target);
            return set;
        }

        @Override
        protected Optional<LorcanaSet> findTargetById(Ulid targetId) {
            return lorcanaSetRepository.findById(targetId);
        }

        @Override
        protected void deleteAll(List<MushuSet> entities) {
            mushuSetRepository.deleteAll(entities);
        }

        @Override
        protected void save(MushuSet entity) {
            mushuSetRepository.save(entity);
        }

        @Override
        protected boolean isValid(MushuSetDTO dto) {
            return StringUtils.isNotBlank(dto.key()) && StringUtils.isNotBlank(dto.name());
        }
    }
}
