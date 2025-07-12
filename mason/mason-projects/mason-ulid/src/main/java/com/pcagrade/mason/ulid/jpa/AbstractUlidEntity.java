package com.pcagrade.mason.ulid.jpa;

import com.github.f4b6a3.ulid.Ulid;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@MappedSuperclass
public abstract class AbstractUlidEntity {

    @Id
    @GeneratedValue(generator = "ulid-generator")
    @GenericGenerator(name = "ulid-generator", type = UlidGenerator.class)
    @Column(name = "id", nullable = false, columnDefinition = UlidColumnDefinitions.DEFINITION)
    @Type(UlidType.class)
    private Ulid id;

    public Ulid getId() {
        return  id;
    }

    public void setId(Ulid id) {
        this.id = id;
    }

}
