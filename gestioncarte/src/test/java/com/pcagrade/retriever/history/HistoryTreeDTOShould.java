package com.pcagrade.mason.jpa.revision;

import org.junit.jupiter.api.Test;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig
class HistoryTreeDTOShould {

    @Test
    void fromRevisions_throwsException_whenEmpty() {
        List<RevisionDTO<String>> list = Collections.emptyList();

        assertThatThrownBy(() -> HistoryTreeDTO.fromRevisions(list))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Revision collection must not be empty");
    }

    @Test
    void fromRevisions() {
        var history = HistoryTreeDTO.fromRevisions(List.of(
                new RevisionDTO<>(1, LocalDateTime.now(), RevisionMetadata.RevisionType.INSERT, "", "", "a"),
                new RevisionDTO<>(2, LocalDateTime.now(), RevisionMetadata.RevisionType.UPDATE, "", "", "b"),
                new RevisionDTO<>(3, LocalDateTime.now(), RevisionMetadata.RevisionType.DELETE, "", "", "c")
        ));

        assertThat(history.revision().number()).isEqualTo(3);
        assertThat(history.parents()).hasSize(1)
                .allSatisfy(p -> {
                   assertThat(p.revision().number()).isEqualTo(2);
                   assertThat(p.parents()).hasSize(1)
                           .allSatisfy(pp -> {
                               assertThat(pp.revision().number()).isEqualTo(1);
                               assertThat(pp.parents()).isEmpty();
                           });
                });
    }

    @Test
    void merge() {
        var history1 = HistoryTreeDTO.fromRevisions(List.of(
                new RevisionDTO<>(1, LocalDateTime.now(), RevisionMetadata.RevisionType.INSERT, "", "", "a"),
                new RevisionDTO<>(2, LocalDateTime.now(), RevisionMetadata.RevisionType.UPDATE, "", "", "b"),
                new RevisionDTO<>(3, LocalDateTime.now(), RevisionMetadata.RevisionType.DELETE, "", "", "c")
        ));
        var history2 = HistoryTreeDTO.fromRevisions(List.of(
                new RevisionDTO<>(2, LocalDateTime.now(), RevisionMetadata.RevisionType.INSERT, "", "", "d"),
                new RevisionDTO<>(3, LocalDateTime.now(), RevisionMetadata.RevisionType.UPDATE, "", "", "c"),
                new RevisionDTO<>(4, LocalDateTime.now(), RevisionMetadata.RevisionType.UPDATE, "", "", "e")
        ));
        var history = history2.merge(history1, s -> s.equals("c"));

        assertThat(history.revision().number()).isEqualTo(4);
        assertThat(history.parents()).hasSize(1)
                .allSatisfy(p -> assertThat(p.parents()).hasSize(2));
    }

}
