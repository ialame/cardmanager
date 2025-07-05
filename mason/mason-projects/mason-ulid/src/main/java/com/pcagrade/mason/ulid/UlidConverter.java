package com.pcagrade.mason.ulid;

import com.github.f4b6a3.ulid.Ulid;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UlidConverter implements Converter<String, Ulid> {

    @Override
    public Ulid convert(@Nullable String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        return Ulid.from(source);
    }
}
