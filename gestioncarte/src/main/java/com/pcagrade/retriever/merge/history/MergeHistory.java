package com.pcagrade.retriever.merge.history;

import com.github.f4b6a3.ulid.Ulid;
import com.pcagrade.mason.ulid.jpa.UlidColumnDefinitions;
import com.pcagrade.mason.ulid.jpa.UlidType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "j_merge_history")
public class MergeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column(columnDefinition = UlidColumnDefinitions.DEFAULT_NULL)
    @Type(UlidType.class)
    private Ulid from;
    @Column(columnDefinition = UlidColumnDefinitions.DEFAULT_NULL)
    @Type(UlidType.class)
    private Ulid to;
    @Column
    private int revision;
    @Column
    public String table;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Ulid getFrom() {
        return from;
    }

    public void setFrom(Ulid from) {
        this.from = from;
    }

    public Ulid getTo() {
        return to;
    }

    public void setTo(Ulid to) {
        this.to = to;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
