package com.pcagrade.retriever.card.lorcana.source.mushu;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.retriever.card.lorcana.LorcanaCardDTO;
import com.pcagrade.retriever.card.lorcana.source.mushu.set.MushuSetService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MushuService {

    @Autowired
    private MushuSetService mushuSetService;
    @Autowired
    private MushuMapper mushuMapper;
    @Autowired
    private MushuParser mushuParser;

    public List<LorcanaCardDTO> extractCards(Ulid setId) {
        return mushuSetService.getMushuSets(setId).stream()
                .flatMap(s -> mushuParser.getCards(s.key()).stream()
                        .filter(c -> StringUtils.equalsIgnoreCase(c.set(), s.name())))
                .map(mushuMapper::mapToDTO)
                .toList();
    }
}
