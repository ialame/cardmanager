package com.pcagrad.magic.util;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;

import java.util.UUID;

public class UlidUtils {

    /**
     * Génère un nouveau ULID sous forme d'UUID
     */
    public static UUID generateUlidAsUuid() {
        return UlidCreator.getUlid().toUuid();
    }

    /**
     * Génère un nouveau ULID sous forme de String
     */
    public static String generateUlidAsString() {
        return UlidCreator.getUlid().toString();
    }

    /**
     * Convertit un UUID en ULID (si l'UUID était originellement un ULID)
     */
    public static Ulid fromUuid(UUID uuid) {
        return Ulid.from(uuid);
    }

    /**
     * Vérifie si un UUID est un ULID valide
     */
    public static boolean isValidUlid(UUID uuid) {
        try {
            Ulid.from(uuid);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Récupère le timestamp d'un ULID
     */
    public static long getTimestamp(UUID ulidAsUuid) {
        try {
            Ulid ulid = Ulid.from(ulidAsUuid);
            return ulid.getTime();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }
}