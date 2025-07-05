package com.pcagrade.retriever.image;

import com.github.f4b6a3.ulid.Ulid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Ulid> {
    Optional<Image> findById(Ulid id);
}
