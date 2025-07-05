package com.pcagrade.mason.ulid.jpa;

import com.github.f4b6a3.ulid.UlidCreator;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class UlidGenerator implements IdentifierGenerator {
    @Override
    public synchronized Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return UlidCreator.getMonotonicUlid();
    }
}
