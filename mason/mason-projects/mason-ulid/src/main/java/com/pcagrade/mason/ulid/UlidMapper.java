package com.pcagrade.mason.ulid;

import com.github.f4b6a3.ulid.Ulid;
import org.apache.commons.lang3.StringUtils;

public class UlidMapper {

    public String toString(Ulid ulid) {
        if (ulid == null) {
            return "";
        }
        return ulid.toString();
    }

    public Ulid fromString(String ulid) {
        if (StringUtils.isBlank(ulid)) {
            return null;
        }
        try {
            return Ulid.from(ulid);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
