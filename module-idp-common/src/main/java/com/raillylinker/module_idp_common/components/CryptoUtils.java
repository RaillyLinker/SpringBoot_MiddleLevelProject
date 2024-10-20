package com.raillylinker.module_idp_common.components;

import org.springframework.lang.NonNull;

public interface CryptoUtils {
    // (AES256 암호화)
    String encryptAES256(
            @NonNull final String text, // 암호화하려는 평문
            @NonNull final String alg, // 암호화 알고리즘 (ex : "AES/CBC/PKCS5Padding")
            @NonNull final String initializationVector, // 초기화 벡터 16byte = 16char
            @NonNull final String encryptionKey // 암호화 키 32byte = 32char
    );

    // (AES256 복호화)
    String decryptAES256(
            @NonNull final String cipherText, // 복호화하려는 암호문
            @NonNull final String alg, // 암호화 알고리즘 (ex : "AES/CBC/PKCS5Padding")
            @NonNull final String initializationVector, // 초기화 벡터 16byte = 16char
            @NonNull final String encryptionKey // 암호화 키 32byte = 32char
    );
}
