package com.pcagrade.mason.ulid;

import com.github.f4b6a3.ulid.Ulid;

public class UlidHelper {

    private UlidHelper() {}

    public static int compare(Ulid id1, String id2) {
        return compare(id1, Ulid.from(id2));
    }

    public static int compare(String id1, Ulid id2) {
        return compare(Ulid.from(id1), id2);
    }

    public static int compare(Ulid id1, Ulid id2) {
        if (id1 == id2) {
            return 0;
        }
        if (id1 == null) {
            return -1;
        }
        if (id2 == null) {
            return 1;
        }
        return id1.compareTo(id2);
    }

    public static boolean equals(Ulid id1, String id2) {
        return compare(id1, id2) == 0;
    }

    public static boolean equals(String id1, Ulid id2) {
        return compare(id1, id2) == 0;
    }

    public static boolean equals(Ulid id1, Ulid id2) {
        return compare(id1, id2) == 0;
    }
}
