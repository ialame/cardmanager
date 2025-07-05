package com.pcagrade.mason.jpa.revision;

import com.pcagrade.mason.jpa.revision.message.RevisionMessageConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfigurationPackage
@Import({ RevisionMessageConfiguration.class })
public class MasonRevisionConfiguration {

}
