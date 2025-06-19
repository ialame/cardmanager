package com.pcagrad.magic;

import com.pcagrad.magic.util.UlidUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CardMagicApplicationTests {

    @Test
    void contextLoads() {
    }
    @Test
    void testUlidGeneration() {
        UUID ulid1 = UlidUtils.generateUlidAsUuid();
        UUID ulid2 = UlidUtils.generateUlidAsUuid();

        // Les ULID doivent être ordonnés chronologiquement
        assertTrue(ulid1.compareTo(ulid2) < 0);

        // Vérifier que c'est bien un ULID valide
        assertTrue(UlidUtils.isValidUlid(ulid1));

        // Le timestamp doit être récent
        long timestamp = UlidUtils.getTimestamp(ulid1);
        assertTrue(timestamp > System.currentTimeMillis() - 1000);
    }

}
