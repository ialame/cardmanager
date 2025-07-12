package com.pcagrade.retriever.image;

import com.pcagrade.mason.localization.Localization;
import com.pcagrade.mason.ulid.jpa.AbstractUlidEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "image")
public class Image extends AbstractUlidEntity {

    @Column(name = "source")
    private String source;

    @Column(name = "path")
    private String path;

    @Column(name = "internal")
    private boolean internal;

    // Getters et setters

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }
}