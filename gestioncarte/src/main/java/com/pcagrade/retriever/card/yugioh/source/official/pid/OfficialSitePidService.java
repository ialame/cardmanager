package com.pcagrade.retriever.card.yugioh.source.official.pid;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.jpa.revision.message.RevisionMessage;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSet;
import com.pcagrade.retriever.card.yugioh.set.YuGiOhSetRepository;
import com.pcagrade.retriever.data.AbstractListDataManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OfficialSitePidService {

    @Autowired
    private OfficialSitePidRepository officialSitePidRepository;
    @Autowired
    private OfficialSitePidMapper officialSitePidMapper;
    @Autowired
    private YuGiOhSetRepository yuGiOhSetRepository;

    private final PidManager pidManager = new PidManager();

    public List<OfficialSitePidDTO> getPids(Ulid setId) {
        return pidManager.getList(setId);
    }

    @RevisionMessage("Modification des PIDs pour l''extension {0}")
    public void setPids(Ulid setId, List<OfficialSitePidDTO> pids) {
        pidManager.setList(setId, pids);
    }

    public Ulid getSetId(OfficialSitePidDTO pid) {
        return officialSitePidRepository.findSetId(pid.pid(), pid.localization());
    }

    private class PidManager extends AbstractListDataManager<OfficialSitePid, OfficialSitePidDTO, YuGiOhSet, Ulid> {

        @Override
        protected List<OfficialSitePid> getByTargetId(Ulid targetId) {
            return officialSitePidRepository.findAllBySetId(targetId);
        }

        @Override
        protected OfficialSitePidDTO mapToDTO(OfficialSitePid entity) {
            return officialSitePidMapper.mapToDTO(entity);
        }

        @Override
        protected OfficialSitePid mapToEntity(OfficialSitePidDTO dto, YuGiOhSet target) {
            var pid = new OfficialSitePid();

            officialSitePidMapper.update(pid, dto);
            pid.setSet(target);
            return pid;
        }

        @Override
        protected Optional<YuGiOhSet> findTargetById(Ulid targetId) {
            return yuGiOhSetRepository.findById(targetId);
        }

        @Override
        protected void deleteAll(List<OfficialSitePid> entities) {
            officialSitePidRepository.deleteAll(entities);
        }

        @Override
        protected void save(OfficialSitePid entity) {
            officialSitePidRepository.save(entity);
        }

        @Override
        protected boolean isValid(OfficialSitePidDTO dto) {
            return dto != null && StringUtils.isNotBlank(dto.pid()) && dto.localization() != null;
        }

        @Override
        protected boolean equals(OfficialSitePidDTO dto1, OfficialSitePidDTO dto2) {
            return StringUtils.equals(dto1.pid(), dto2.pid()) && dto1.localization() == dto2.localization();
        }
    }
}
