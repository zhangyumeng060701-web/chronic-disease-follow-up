/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * SensitiveDataCipher 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Component
public class SensitiveDataCipher {
    private static final String PREFIX = "enc:";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BITS = 128;

    @Value("${data.encryption.key:}")
    private String encryptionKey;

/**
 * 执行 isConfigured 操作。
 */
    public boolean isConfigured() {
        return StringUtils.hasText(encryptionKey);
    }

    public String encrypt(String plainText) {
        if (!StringUtils.hasText(plainText) || !StringUtils.hasText(encryptionKey)) {
            return plainText;
        }
        try {
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, buildKey(), new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] payload = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, payload, 0, iv.length);
            System.arraycopy(cipherText, 0, payload, iv.length, cipherText.length);
            return PREFIX + Base64.getEncoder().encodeToString(payload);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new IllegalStateException("敏感字段加密失败", e);
        }
    }

/**
 * 执行 decrypt 操作。
 */
    public String decrypt(String encryptedText) {
        if (!StringUtils.hasText(encryptedText) || !encryptedText.startsWith(PREFIX)) {
            return encryptedText;
        }
        if (!StringUtils.hasText(encryptionKey)) {
            return encryptedText;
        }
        try {
            byte[] payload = Base64.getDecoder().decode(encryptedText.substring(PREFIX.length()));
            byte[] iv = Arrays.copyOfRange(payload, 0, IV_LENGTH);
            byte[] cipherText = Arrays.copyOfRange(payload, IV_LENGTH, payload.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, buildKey(), new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            return encryptedText;
        }
    }

    private SecretKeySpec buildKey() throws GeneralSecurityException {
        byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(encryptionKey.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(digest, "AES");
    }
}
