package com.pcagrade.painter;

import com.pcagrade.mason.test.oauth2.MasonOAuth2Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@MasonOAuth2Test
@TestPropertySource(locations = {"/application.yaml", "/application-test.yaml"})
public @interface PainterIntegrationTest {
}
