package com.pcagrad.magic.entity;

import com.pcagrad.magic.util.UlidGenerator;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@MappedSuperclass
public abstract class AbstractUuidEntity {

    @Id
    @GeneratedValue(generator = "ulid-generator")
    @GenericGenerator(name = "ulid-generator", strategy = "com.pcagrad.magic.util.UlidGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Méthode utilitaire pour obtenir l'ID sous forme de String ULID
     */
    @Transient
    public String getUlidString() {
        if (id == null) return null;
        try {
            return com.github.f4b6a3.ulid.Ulid.from(id).toString();
        } catch (Exception e) {
            return id.toString();
        }
    }

    /**
     * Méthode utilitaire pour obtenir le timestamp de création
     */
    @Transient
    public long getCreationTimestamp() {
        if (id == null) return System.currentTimeMillis();
        try {
            return com.github.f4b6a3.ulid.Ulid.from(id).getTime();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }
}