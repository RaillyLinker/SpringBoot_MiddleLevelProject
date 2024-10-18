package raillylinker.module_idp_common.custom_classes;

import org.springframework.lang.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

// [암호화 함수 모음 클래스]
public class CryptoUtils {
    // AES256 암호화
    public static String encryptAES256(
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

    // AES256 복호화
    public static String decryptAES256(
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

    // Base64 인코딩
    public static String base64Encode(@NonNull final String str) {
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    // Base64 디코딩
    public static String base64Decode(@NonNull final String str) {
        return new String(Base64.getDecoder().decode(str));
    }

    // SHA256 해싱
    public static String hashSHA256(@NonNull final String str) {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            return CustomUtils.bytesToHex(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // HmacSHA256
    public static String hmacSha256(@NonNull final String data, @NonNull final String secret) {
        try {
            final Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            sha256Hmac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
