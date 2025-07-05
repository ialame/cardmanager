package com.pcagrade.mason.ulid.jpa;

import com.github.f4b6a3.ulid.Ulid;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ByteArrayJavaType;

public class UlidJavaType extends AbstractClassJavaType<Ulid> {

    public static final UlidJavaType INSTANCE = new UlidJavaType();

    private UlidJavaType() {
        super(Ulid.class);
    }

    @Override
    public <X> X unwrap(Ulid value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        return ByteArrayJavaType.INSTANCE.unwrap(ArrayUtils.toObject(value.toBytes()), type, options);
    }

    @Override
    public <X> Ulid wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        return Ulid.from(ArrayUtils.toPrimitive(ByteArrayJavaType.INSTANCE.wrap(value, options)));
    }
}
