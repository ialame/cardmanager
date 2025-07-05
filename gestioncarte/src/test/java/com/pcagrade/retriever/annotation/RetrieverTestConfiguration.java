package com.pcagrade.retriever.annotation;

import com.pcagrade.retriever.RetrieverTestConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TestConfiguration
@Import(RetrieverTestConfig.class)
public @interface RetrieverTestConfiguration {

}
