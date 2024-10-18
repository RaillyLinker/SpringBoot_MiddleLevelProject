package raillylinker.module_idp_common.custom_classes;

import org.springframework.lang.NonNull;

// [커스텀 함수 모음 클래스]
public class CustomUtils {
    // (byteArray 를 Hex String 으로 반환)
    public static String bytesToHex(@NonNull final byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        for (final byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
