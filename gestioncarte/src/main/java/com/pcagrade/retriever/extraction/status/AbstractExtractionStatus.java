package com.pcagrade.retriever.extraction.status;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import org.hibernate.envers.Audited;

@MappedSuperclass
@Audited
public abstract class AbstractExtractionStatus<T> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_id")
    private T target;

    @Lob
    @Column(name = "ignored_locales", columnDefinition = "LONGTEXT")
    private String ignoredLocalizations;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target = target;
    }

    public String getIgnoredLocalizations() {
        return ignoredLocalizations;
    }

    public void setIgnoredLocalizations(String ignoredLocalizations) {
        this.ignoredLocalizations = ignoredLocalizations;
    }
}
