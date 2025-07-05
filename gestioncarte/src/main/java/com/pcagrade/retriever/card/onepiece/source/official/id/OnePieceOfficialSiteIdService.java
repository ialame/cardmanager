package com.pcagrade.retriever.card.onepiece.source.official.id;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.retriever.card.onepiece.set.OnePieceSet;
import com.pcagrade.retriever.card.onepiece.set.OnePieceSetRepository;
import com.pcagrade.retriever.data.AbstractListDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OnePieceOfficialSiteIdService {

    @Autowired
    private OnePieceOfficialSiteIdRepository onePieceOfficialSiteIdRepository;
    @Autowired
    private OnePieceOfficialSiteIdMapper onePieceOfficialSiteIdMapper;
    @Autowired
    private OnePieceSetRepository onePieceSetRepository;

    private final IdManager idManager = new IdManager();

    public List<OnePieceOfficialSiteIdDTO> getIds(Ulid setId) {
        return idManager.getList(setId);
    }

    @RevisionMessage("Modification de IDs du site officiel pour l''extension {0}")
    public void setIds(Ulid setId, List<OnePieceOfficialSiteIdDTO> ids) {
        idManager.setList(setId, ids);
    }

    private class IdManager extends AbstractListDataManager<OnePieceOfficialSiteId, OnePieceOfficialSiteIdDTO, OnePieceSet, Ulid> {

        @Override
        protected List<OnePieceOfficialSiteId> getByTargetId(Ulid targetId) {
            return onePieceOfficialSiteIdRepository.findAllBySetId(targetId);
        }

        @Override
        protected OnePieceOfficialSiteIdDTO mapToDTO(OnePieceOfficialSiteId entity) {
            return onePieceOfficialSiteIdMapper.mapToDTO(entity);
        }

        @Override
        protected OnePieceOfficialSiteId mapToEntity(OnePieceOfficialSiteIdDTO dto, OnePieceSet target) {
            var id = new OnePieceOfficialSiteId();

            onePieceOfficialSiteIdMapper.update(id, dto);
            id.setSet(target);
            return id;
        }

        @Override
        protected Optional<OnePieceSet> findTargetById(Ulid targetId) {
            return onePieceSetRepository.findById(targetId);
        }

        @Override
        protected void deleteAll(List<OnePieceOfficialSiteId> entities) {
            onePieceOfficialSiteIdRepository.deleteAll(entities);
        }

        @Override
        protected void save(OnePieceOfficialSiteId entity) {
            onePieceOfficialSiteIdRepository.save(entity);
        }

        @Override
        protected boolean isValid(OnePieceOfficialSiteIdDTO dto) {
            return dto != null && dto.id() != 0 && dto.localization() != null;
        }

        @Override
        protected boolean equals(OnePieceOfficialSiteIdDTO dto1, OnePieceOfficialSiteIdDTO dto2) {
            return dto1.id() == dto2.id() && dto1.localization() == dto2.localization();
        }
    }
}
