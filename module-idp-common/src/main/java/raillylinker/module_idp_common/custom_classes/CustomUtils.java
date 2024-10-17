package raillylinker.module_idp_common.custom_classes;

// [커스텀 함수 모음 클래스]
public class CustomUtils {
    // (byteArray 를 Hex String 으로 반환)
    public static String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
