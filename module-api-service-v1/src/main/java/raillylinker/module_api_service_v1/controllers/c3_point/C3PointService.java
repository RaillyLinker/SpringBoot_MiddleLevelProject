package raillylinker.module_api_service_v1.controllers.c3_point;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import raillylinker.module_api_service_v1.datasources.memory_const_object.ProjectConst;
import raillylinker.module_idp_common.custom_classes.CryptoUtils;

@Service
public class C3PointService {
    // <멤버 변수 공간>
    // 프로젝트 실행 시 사용 설정한 프로필명 (설정 안하면 default 반환)
    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    private final Logger classLogger = LoggerFactory.getLogger(this.getClass());

    // 토스페이 시크릿 키
    @Value("${custom-config.toss-pay-secret-key}")
    private String tossPaySecretKey;

    // 포인트 전환 비율(추후 비율 변경을 가정 - 별도의 설정용 DB 에 저장하는 식의 개선 가능)
    private final Double POINT_CONVERSION_RATE = 1.0;


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (Toss 기반 포인트 결제 함수)
    public C3PointController.Api1TossPayServicePointOutputVo api1TossPayServicePoint(
            HttpServletResponse httpServletResponse,
            String freelancerUid,
            C3PointController.Api1TossPayServicePointInputVo inputVo
    ) {
        // 받은 프리렌서 고유값 복호화 작업(인증 정보 검증 부분이라 생각하시면 됩니다.)
        long freelancerUidLong;
        try {
            String decodedUid = CryptoUtils.decryptAES256(
                    freelancerUid,
                    "AES/CBC/PKCS5Padding",
                    ProjectConst.SERVER_SECRET_IV,
                    ProjectConst.SERVER_SECRET_SECRET_KEY
            );

            // String 타입 uid 파라미터를 Long 으로 변경
            freelancerUidLong = Long.parseLong(decodedUid);
        } catch (Exception e) {
            // 검증 실패
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            return null;
        }

        // toss 에 secretkey, paymentkey, orderid, amount 로 결제 승인 요청
        NetworkLibrary.NetworkResult networkResult =
                NetworkLibrary.tossRequest(tossPaySecretKey, inputVo.paymentKey(), inputVo.orderId(), inputVo.orderAmount());

        // toss 에서 받아온 결제 승인 정보를 바탕으로 처리
        switch (networkResult) {
            case OK -> {
                // 결제 완료
                // 결제 금액을 포인트 비율로 변환
                Double paidPoint = inputVo.orderAmount() * POINT_CONVERSION_RATE;
                // todo DB 저장(이때도 에러 발생을 대비하여 결제 취소 로직 사용)

                return new C3PointController.Api1TossPayServicePointOutputVo(1);
            }
            case FAILED -> {
                // 결제 실패
                return new C3PointController.Api1TossPayServicePointOutputVo(2);
            }
            default -> {
                // 네트워크 에러
                return new C3PointController.Api1TossPayServicePointOutputVo(3);
            }
        }
    }

    // (WebClient 와 같은 네트워크 요청 라이브러리를 가정)
    private static class NetworkLibrary {
        // (Toss Payments 결제 승인 요청 API 를 가정)
        private static NetworkResult tossRequest(String secretKey, String paymentKey, String orderId, Long amount) {
            return NetworkResult.OK;
        }

        // (네트워크 요청 결과)
        enum NetworkResult {
            OK, // 결제 성공
            FAILED, // 결제 실패
            NETWORK_ERROR // 네트워크 에러
        }
    }
}
