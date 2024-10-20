package com.raillylinker.module_api_service_v1.services.Impl;

import com.raillylinker.module_api_service_v1.controllers.C3PointController;
import com.raillylinker.module_api_service_v1.services.C3PointService;
import com.raillylinker.module_idp_common.components.CryptoUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.raillylinker.module_api_service_v1.const_objects.ProjectConfigs;
import com.raillylinker.module_idp_jpa.jpa_beans.entities.MiddleLevelSpringbootProject1_Freelancer;
import com.raillylinker.module_idp_jpa.jpa_beans.entities.MiddleLevelSpringbootProject1_ServicePoint;
import com.raillylinker.module_idp_jpa.jpa_beans.entities.MiddleLevelSpringbootProject1_ServicePointPaymentHistory;
import com.raillylinker.module_idp_jpa.jpa_beans.entities.MiddleLevelSpringbootProject1_ServicePointPaymentTossPayInfo;
import com.raillylinker.module_idp_jpa.jpa_beans.repositories.MiddleLevelSpringbootProject1_FreelancerRepository;
import com.raillylinker.module_idp_jpa.jpa_beans.repositories.MiddleLevelSpringbootProject1_ServicePointPaymentHistoryRepository;
import com.raillylinker.module_idp_jpa.jpa_beans.repositories.MiddleLevelSpringbootProject1_ServicePointPaymentTossPayInfoRepository;
import com.raillylinker.module_idp_jpa.jpa_beans.repositories.MiddleLevelSpringbootProject1_ServicePointRepository;
import com.raillylinker.module_idp_jpa.jpa_beans.repositories_dsl.impl.MiddleLevelSpringbootProject1_RepositoryDslImpl;

import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.Semaphore;

@Service
public class C3PointServiceImpl implements C3PointService {
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

    @Autowired
    CryptoUtils cryptoUtils;

    @Autowired
    MiddleLevelSpringbootProject1_FreelancerRepository middleLevelSpringbootProject1FreelancerRepository;

    @Autowired
    MiddleLevelSpringbootProject1_ServicePointRepository middleLevelSpringbootProject1ServicePointRepository;

    @Autowired
    MiddleLevelSpringbootProject1_ServicePointPaymentHistoryRepository middleLevelSpringbootProject1ServicePointPaymentHistoryRepository;

    @Autowired
    MiddleLevelSpringbootProject1_ServicePointPaymentTossPayInfoRepository middleLevelSpringbootProject1ServicePointPaymentTossPayInfoRepository;

    @Autowired
    MiddleLevelSpringbootProject1_RepositoryDslImpl middleLevelSpringbootProject1RepositoryDsl;

    // (동시 결제 방지 처리)
    // 처리 방식 : 현재 결제 처리가 진행중인 유저의 uid 를 저장하고, 결제가 진행중이라면 return 아니라면 유저 uid 를 저장하고 진행합니다.
    //     비동기 처리를 위해 세마포어를 걸어두었습니다.
    //     위와 같은 방식으로는 Scale out 이 불가능하므로 추후 Redis 와 같은 공유 메모리에 이를 적용하면 됩니다.
    // 현재 결제 진행중인 프리랜서 uid(암호화) 리스트
    LinkedList<String> nowFreelancerUidList = new LinkedList<>();
    // nowFreelancerUidList 비동기 처리를 위한 세마포어, 추후 ScaleOut 을 위해서 분산락을 사용하면 좋습니다.
    Semaphore freelancerUidListSemaphore = new Semaphore(1);


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (Toss 기반 포인트 결제 함수)
    @Transactional
    public C3PointController.Api1TossPayServicePointOutputVo api1TossPayServicePoint(
            HttpServletResponse httpServletResponse,
            String freelancerUid,
            C3PointController.Api1TossPayServicePointInputVo inputVo
    ) {
        // 같은 유저가 동시에 결제를 진행하는 것을 방지
        try {
            freelancerUidListSemaphore.acquire();
            if (nowFreelancerUidList.contains(freelancerUid)) {
                // 결제 진행중인 프리랜서 => 결제 진행이 불가능하도록 처리
                return new C3PointController.Api1TossPayServicePointOutputVo(4);
            } else {
                // 결제 진행중이 아님 -> 결제 진행중으로 변경
                nowFreelancerUidList.add(freelancerUid);
            }
        } catch (Exception e) {
            classLogger.error(e.toString());
        } finally {
            freelancerUidListSemaphore.release();
        }

        try {
            // 받은 프리렌서 고유값 복호화 작업(인증 정보 검증 부분이라 생각하시면 됩니다.)
            long freelancerUidLong;
            try {
                String decodedUid = cryptoUtils.decryptAES256(
                        freelancerUid,
                        "AES/CBC/PKCS5Padding",
                        ProjectConfigs.SERVER_SECRET_IV,
                        ProjectConfigs.SERVER_SECRET_SECRET_KEY
                );
                // String 타입 uid 파라미터를 Long 으로 변경
                freelancerUidLong = Long.parseLong(decodedUid);
            } catch (Exception e) {
                // 검증 실패
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                return null;
            }

            // Freelancer 정보를 찾아옵니다.
            Optional<MiddleLevelSpringbootProject1_Freelancer> freelancerOptional =
                    middleLevelSpringbootProject1FreelancerRepository.findByUidAndRowDeleteDateStr(freelancerUidLong, "/");
            if (freelancerOptional.isEmpty()) {
                // 데이터에 저장된 프리랜서 정보가 없음 == 검증 실패와 동일
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                return null;
            }
            MiddleLevelSpringbootProject1_Freelancer freelancer = freelancerOptional.get();

            // toss 에 secretkey, paymentkey, orderid, amount 로 결제 승인 요청
            NetworkLibrary.NetworkResult networkResult =
                    NetworkLibrary.tossRequest(tossPaySecretKey, inputVo.paymentKey(), inputVo.orderId(), inputVo.orderAmount());

            // toss 에서 받아온 결제 승인 정보를 바탕으로 처리
            switch (networkResult) {
                case OK -> {
                    // 결제 완료
                    try {
                        // 결제 금액을 포인트 비율로 변환
                        double paidPoint = inputVo.orderAmount() * POINT_CONVERSION_RATE;
                        paidPoint = extraPointEvent(paidPoint);

                        // ServicePoint 정보를 찾아옵니다.
                        Optional<MiddleLevelSpringbootProject1_ServicePoint> servicePointOpt =
                                middleLevelSpringbootProject1ServicePointRepository.findByFreelancerAndRowDeleteDateStr(freelancer, "/");

                        MiddleLevelSpringbootProject1_ServicePoint servicePoint;
                        if (servicePointOpt.isEmpty()) {
                            // ServicePoint 가 존재하지 않음
                            // ServicePoint 생성
                            servicePoint = new MiddleLevelSpringbootProject1_ServicePoint(paidPoint, freelancer);
                        } else {
                            // ServicePoint 가 존재함
                            // 기존 서비스 포인트에 추가
                            servicePoint = servicePointOpt.get();
                            servicePoint.servicePoint += paidPoint;
                        }

                        // DB 저장
                        middleLevelSpringbootProject1ServicePointRepository.save(servicePoint);

                        // 결제 히스토리 저장
                        MiddleLevelSpringbootProject1_ServicePointPaymentHistory middleLevelSpringbootProject1ServicePointPaymentHistory =
                                middleLevelSpringbootProject1ServicePointPaymentHistoryRepository.save(
                                        new MiddleLevelSpringbootProject1_ServicePointPaymentHistory(
                                                MiddleLevelSpringbootProject1_ServicePointPaymentHistory.PaymentType.TOSS_PAY,
                                                inputVo.orderAmount(),
                                                paidPoint,
                                                freelancer
                                        )
                                );

                        // 토스 페이 결제 타입 정보(결제 히스토리 고유번호, 환불을 위하여 결제 취소에 필요한 정보) 저장
                        middleLevelSpringbootProject1ServicePointPaymentTossPayInfoRepository.save(
                                new MiddleLevelSpringbootProject1_ServicePointPaymentTossPayInfo(
                                        inputVo.paymentKey(),
                                        inputVo.orderId(),
                                        middleLevelSpringbootProject1ServicePointPaymentHistory
                                )
                        );

                        // 트랜젝션 동작 테스트
//                    throw new RuntimeException("payment complete block error");
                    } catch (Exception e) {
                        // 에러 발생 => 결제 취소
                        NetworkLibrary.NetworkResult cancelNetworkResult =
                                NetworkLibrary.tossCancelRequest(tossPaySecretKey, inputVo.paymentKey(), inputVo.orderId(), inputVo.orderAmount());
                        switch (cancelNetworkResult) {
                            case OK -> {
                                // 취소 성공 = 결제 실패
                                // 트랜젝션 롤백 발동을 위한 RuntimeException
                                throw new RuntimeException("payment complete block error");
                            }
                            default -> {
                                // 취소 실패 or 네트워크 에러

                                // 취소 실패 처리는 아래와 같이 합니다.
                                // 취소까지 실패한 경우에 대한 처리 설계
                                // 토스 환불 실패 정보(결제 취소에 필요한 정보)저장
                                // -> 담당자에게 정보 전달 -> 담당자가 수동으로 처리

                                // 트랜젝션 롤백 발동을 위한 RuntimeException
                                throw new RuntimeException("payment complete block error");
                            }
                        }
                    }

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
        } finally {
            // 같은 유저가 동시에 결제를 진행하는 것을 방지 해제
            try {
                freelancerUidListSemaphore.acquire();
                nowFreelancerUidList.remove(freelancerUid);
            } catch (Exception e) {
                classLogger.error(e.toString());
            } finally {
                freelancerUidListSemaphore.release();
            }
        }
    }

    // (포인트 적립 이벤트 적용 함수)
    // 추가 포인트 이벤트 기능 설계 :
    //     이벤트의 경우 적용 방식이 상이할 수 있으므로 추후 정형화된 이벤트라면 그때가서 설계하고,
    //     일회성의 특별한 이벤트의 경우는 이벤트 구현 후 재배포를 하는 방식을 사용할 것입니다.
    private static double extraPointEvent(double servicePoint) {
        // 10% 추가 포인트 적용
//        double eventResultPoint = servicePoint + (servicePoint * 0.1);

        return servicePoint;
    }

    // (WebClient 와 같은 네트워크 요청 라이브러리를 가정한 pseudo 코드)
    // 객체 지향적으로 분리 (정식 구현시 네트워크 요청 모듈로 떼어둡니다.)
    private static class NetworkLibrary {
        // (Toss Payments 결제 승인 요청 API 를 가정한 pseudo 코드)
        private static NetworkResult tossRequest(String secretKey, String paymentKey, String orderId, Long amount) {
            return NetworkResult.OK;
        }

        // (Toss Payments 결제 승인 취소 API 를 가정한 pseudo 코드)
        private static NetworkResult tossCancelRequest(String secretKey, String paymentKey, String orderId, Long amount) {
            return NetworkResult.OK;
        }

        // (네트워크 요청 결과)
        enum NetworkResult {
            OK, // 결제 성공
            FAILED, // 결제 실패
            NETWORK_ERROR // 네트워크 에러
        }
    }

    // 추가 : 할인 쿠폰 기능을 추가하려면 아래와 같이 합니다.
    // 할인 쿠폰 기능 설계
    //     서비스 포인트 할인 쿠폰 정보(고유번호, 할인 비율(백분율), 고정 할인값(할인 비율 적용 이후에 적용), 최대 할인가) 데이터베이스 존재
    //     -> 결제 api 로 들어온 쿠폰 일련번호 검증(존재 여부, 적합성 여부)
    //     -> 할인가 적용
    //     -> 결제 완료 후 할인 쿠폰 데이터베이스에서 삭제
    // 할인 쿠폰에 대한 동시 접근 제어 처리도 해야합니다.
}
