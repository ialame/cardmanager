package com.pcagrade.mason.ulid.jpa;

import com.github.f4b6a3.ulid.Ulid;
import org.hibernate.HibernateException;
import org.hibernate.type.descriptor.java.BasicJavaType;
import org.hibernate.type.descriptor.jdbc.BinaryJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.usertype.BaseUserTypeSupport;
import org.hibernate.usertype.EnhancedUserType;

import java.util.function.BiConsumer;

public class UlidType extends BaseUserTypeSupport<Ulid> implements EnhancedUserType<Ulid> {
    @Override
    protected void resolve(BiConsumer<BasicJavaType<Ulid>, JdbcType> resolutionConsumer) {
        resolutionConsumer.accept(UlidJavaType.INSTANCE, BinaryJdbcType.INSTANCE);
    }

    @Override
    public String toSqlLiteral(Ulid value) {
        var bytes = value.toBytes();
        var builder = new StringBuilder();

        builder.append("x'");
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        builder.append("'");
        return builder.toString();
    }

    @Override
    public String toString(Ulid value) throws HibernateException {
        return value.toString();
    }

    @Override
    public Ulid fromStringValue(CharSequence sequence) throws HibernateException {
        return Ulid.from(sequence.toString());
    }
}
