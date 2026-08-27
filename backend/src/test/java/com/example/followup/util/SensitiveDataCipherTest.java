package com.example.followup.util;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SensitiveDataCipherTest {
    @Test void encryptDecryptRoundTripSupportsChinese() {
        SensitiveDataCipher cipher = cipher("test-key-one");
        String encrypted = cipher.encrypt("南京市鼓楼区13812345678");
        assertTrue(encrypted.startsWith("enc:"));
        assertEquals("南京市鼓楼区13812345678", cipher.decrypt(encrypted));
    }

    @Test void randomIvProducesDifferentCiphertext() {
        SensitiveDataCipher cipher = cipher("test-key-one");
        assertNotEquals(cipher.encrypt("13812345678"), cipher.encrypt("13812345678"));
    }

    @Test void historicalPlaintextAndEmptyValuesRemainCompatible() {
        SensitiveDataCipher cipher = cipher("test-key-one");
        assertEquals("13812345678", cipher.decrypt("13812345678"));
        assertEquals("", cipher.encrypt(""));
        assertEquals(null, cipher.decrypt(null));
    }

    @Test void missingOrWrongKeyNeverRevealsEncryptedPlaintext() {
        SensitiveDataCipher source = cipher("correct-key");
        String encrypted = source.encrypt("320102199001011234");
        SensitiveDataCipher missing = cipher("");
        SensitiveDataCipher wrong = cipher("wrong-key");
        assertEquals(encrypted, missing.decrypt(encrypted));
        assertNotEquals("320102199001011234", wrong.decrypt(encrypted));
    }

    private SensitiveDataCipher cipher(String key) {
        SensitiveDataCipher cipher = new SensitiveDataCipher();
        ReflectionTestUtils.setField(cipher, "encryptionKey", key);
        return cipher;
    }
}
