package com.pcagrade.retriever.annotation;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SpringJUnitConfig
@TestPropertySource(locations = {"/application.properties", "/application-test.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public @interface RetrieverTest {

	/**
	 * Alias for {@link SpringJUnitConfig#classes}.
	 */
	@AliasFor(annotation = SpringJUnitConfig.class, attribute = "classes")
	Class<?>[] value() default {};

}
