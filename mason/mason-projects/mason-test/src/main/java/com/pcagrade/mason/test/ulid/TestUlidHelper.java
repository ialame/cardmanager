package com.pcagrade.mason.test.ulid;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;

public class TestUlidHelper {

    private TestUlidHelper() { }

    public static byte[] decode(String ulid) {
        return Ulid.from(ulid).toBytes();
    }

    public static String encode(byte[] bytes) {
        return Ulid.from(bytes).toString();
    }

    public static byte[] create() {
        return UlidCreator.getMonotonicUlid().toBytes();
    }
}
