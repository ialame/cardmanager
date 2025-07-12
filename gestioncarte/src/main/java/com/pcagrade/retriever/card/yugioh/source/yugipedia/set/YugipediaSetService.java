package com.pcagrade.retriever.card.yugioh.source.yugipedia.set;

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
public class YugipediaSetService {

    @Autowired
    private YugipediaSetRepository yugipediaSetRepository;
    @Autowired
    private YugipediaSetMapper yugipediaSetMapper;
    @Autowired
    private YuGiOhSetRepository yuGiOhSetRepository;

    private final PidManager pidManager = new PidManager();

    public List<YugipediaSetDTO> getYugipediaSets(Ulid setId) {
        return pidManager.getList(setId);
    }

    @RevisionMessage("Modification des line yugipedia pour l''extension {0}")
    public void setYugipediaSets(Ulid setId, List<YugipediaSetDTO> pids) {
        pidManager.setList(setId, pids);
    }

    private class PidManager extends AbstractListDataManager<YugipediaSet, YugipediaSetDTO, YuGiOhSet, Ulid> {

        @Override
        protected List<YugipediaSet> getByTargetId(Ulid targetId) {
            return yugipediaSetRepository.findAllBySetId(targetId);
        }

        @Override
        protected YugipediaSetDTO mapToDTO(YugipediaSet entity) {
            return yugipediaSetMapper.mapToDTO(entity);
        }

        @Override
        protected YugipediaSet mapToEntity(YugipediaSetDTO dto, YuGiOhSet target) {
            var pid = new YugipediaSet();

            yugipediaSetMapper.update(pid, dto);
            pid.setSet(target);
            return pid;
        }

        @Override
        protected Optional<YuGiOhSet> findTargetById(Ulid targetId) {
            return yuGiOhSetRepository.findById(targetId);
        }

        @Override
        protected void deleteAll(List<YugipediaSet> entities) {
            yugipediaSetRepository.deleteAll(entities);
        }

        @Override
        protected void save(YugipediaSet entity) {
            yugipediaSetRepository.save(entity);
        }

        @Override
        protected boolean isValid(YugipediaSetDTO dto) {
            return dto != null && StringUtils.isNotBlank(dto.url()) && dto.localization() != null;
        }

        @Override
        protected boolean equals(YugipediaSetDTO dto1, YugipediaSetDTO dto2) {
            return StringUtils.equals(dto1.url(), dto2.url()) && dto1.localization() == dto2.localization();
        }
    }
}
