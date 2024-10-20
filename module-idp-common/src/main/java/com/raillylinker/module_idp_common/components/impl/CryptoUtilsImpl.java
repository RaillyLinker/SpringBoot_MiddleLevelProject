package com.raillylinker.module_idp_common.components.impl;

import com.raillylinker.module_idp_common.components.CryptoUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

// [암호화 함수 모음 클래스]
@Component
public class CryptoUtilsImpl implements CryptoUtils {
    // (AES256 암호화)
    public String encryptAES256(
            @NonNull final String text, // 암호화하려는 평문
            @NonNull final String alg, // 암호화 알고리즘 (ex : "AES/CBC/PKCS5Padding")
            @NonNull final String initializationVector, // 초기화 벡터 16byte = 16char
            @NonNull final String encryptionKey // 암호화 키 32byte = 32char
    ) {
        if (encryptionKey.length() != 32 || initializationVector.length() != 16) {
            throw new RuntimeException("encryptionKey length must be 32 and initializationVector length must be 16");
        }

        try {
            final Cipher cipher = Cipher.getInstance(alg);
            final SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes(), "AES");
            final IvParameterSpec ivParamSpec = new IvParameterSpec(initializationVector.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);
            final byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // (AES256 복호화)
    public String decryptAES256(
            @NonNull final String cipherText, // 복호화하려는 암호문
            @NonNull final String alg, // 암호화 알고리즘 (ex : "AES/CBC/PKCS5Padding")
            @NonNull final String initializationVector, // 초기화 벡터 16byte = 16char
            @NonNull final String encryptionKey // 암호화 키 32byte = 32char
    ) {
        if (encryptionKey.length() != 32 || initializationVector.length() != 16) {
            throw new RuntimeException("encryptionKey length must be 32 and initializationVector length must be 16");
        }

        try {
            final Cipher cipher = Cipher.getInstance(alg);
            final SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes(), "AES");
            final IvParameterSpec ivParamSpec = new IvParameterSpec(initializationVector.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);
            final byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            final byte[] decrypted = cipher.doFinal(decodedBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
