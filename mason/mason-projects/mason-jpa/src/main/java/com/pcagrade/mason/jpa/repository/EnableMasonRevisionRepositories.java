package com.pcagrade.mason.jpa.repository;

import com.pcagrade.mason.jpa.revision.MasonRevisionConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableEnversRepositories(repositoryBaseClass = SimpleMasonRevisionRepository.class)
@Import(MasonRevisionConfiguration.class)
public @interface EnableMasonRevisionRepositories {
}
