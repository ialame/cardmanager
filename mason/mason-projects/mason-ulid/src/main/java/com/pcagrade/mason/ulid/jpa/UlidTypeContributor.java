package com.pcagrade.mason.ulid.jpa;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.service.ServiceRegistry;

public class UlidTypeContributor implements TypeContributor {
    @Override
    public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        typeContributions.contributeJavaType(UlidJavaType.INSTANCE);
        typeContributions.contributeType(new UlidType());
    }

}
