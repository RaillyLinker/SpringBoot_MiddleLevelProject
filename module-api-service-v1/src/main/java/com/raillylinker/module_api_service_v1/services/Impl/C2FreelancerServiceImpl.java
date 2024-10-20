package com.raillylinker.module_api_service_v1.services.Impl;

import com.raillylinker.module_api_service_v1.controllers.C2FreelancerController;
import com.raillylinker.module_api_service_v1.services.C2FreelancerService;
import com.raillylinker.module_idp_common.components.CryptoUtils;
import com.raillylinker.module_idp_jpa.jpa_beans.repositories_dsl.MiddleLevelSpringbootProject1_RepositoryDsl;
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
import com.raillylinker.module_idp_jpa.jpa_beans.entities.MiddleLevelSpringbootProject1_FreelancerView;
import com.raillylinker.module_idp_jpa.jpa_beans.repositories.MiddleLevelSpringbootProject1_FreelancerRepository;
import com.raillylinker.module_idp_jpa.jpa_beans.repositories.MiddleLevelSpringbootProject1_FreelancerViewRepository;
import com.raillylinker.module_idp_jpa.jpa_beans.repositories_dsl.impl.MiddleLevelSpringbootProject1_RepositoryDslImpl;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Service
public class C2FreelancerServiceImpl implements C2FreelancerService {
    // <멤버 변수 공간>
    // 프로젝트 실행 시 사용 설정한 프로필명 (설정 안하면 default 반환)
    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    private final Logger classLogger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CryptoUtils cryptoUtils;

    @Autowired
    MiddleLevelSpringbootProject1_FreelancerRepository middleLevelSpringbootProject1FreelancerRepository;

    @Autowired
    MiddleLevelSpringbootProject1_FreelancerViewRepository middleLevelSpringbootProject1FreelancerViewRepository;

    @Autowired
    MiddleLevelSpringbootProject1_RepositoryDslImpl middleLevelSpringbootProject1RepositoryDsl;

    // (viewCount up 작업용 스레드풀)
    // 메시지 큐를 이용한 작업 분산 처리를 대신하여 사용하였습니다. (코드 단순화를 통한 알고리즘 및 가독성에 집중)
    // 기능 설계 : 작업 발생시 메시지 큐로 이벤트 발송(viewCount up 할 freelancerView uid 전송)
    //     -> 이벤트를 받은 노드 하나(kafka 에서는 동일 groupId, 동일 topic 사용)에서 작업 수행
    ExecutorService viewCountUpThreadPool = Executors.newFixedThreadPool(5);

    // (viewCount up 작업시 데이터 무결성을 위한 락 세마포어)
    // 동시에 한 스레드만 접근 가능하도록 허용하여 작업 완료시까지 대기
    // Redis 를 이용한 분산락을 대신하여 사용하였습니다. (코드 단순화를 통한 알고리즘 및 가독성에 집중)
    // 기능 설계 : 작업 시작 구간에서 Redis 분산 락 요청
    //     -> 작업 시작
    //     -> 분산 락을 획득할 때까지 요청 반복
    //     -> 분산 락을 획득하면 작업 수행
    //     -> try finally 에서 분산 락 해소
    private final Semaphore viewCountUpSemaphore = new Semaphore(1);


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (프리랜서 등록 함수)
    @Transactional
    public C2FreelancerController.Api1InsertFreelancerOutputVo api1InsertFreelancer(
            HttpServletResponse httpServletResponse,
            C2FreelancerController.Api1InsertFreelancerInputVo inputVo
    ) {
        // 프리렌서 입력 정보
        MiddleLevelSpringbootProject1_Freelancer middleLevelSpringbootProject1Freelancer =
                new MiddleLevelSpringbootProject1_Freelancer(inputVo.freelancerName());

        // 프리렌서 정보 등록
        MiddleLevelSpringbootProject1_Freelancer newMiddleLevelSpringbootProject1Freelancer =
                middleLevelSpringbootProject1FreelancerRepository.save(middleLevelSpringbootProject1Freelancer);

        // @Transactional 롤백 테스트 (완료)
//        throw new RuntimeException("강제 예외 발생으로 인해 롤백되어야 함.");

        // 등록된 프리렌서 고유값 암호화
        String encodedUid = cryptoUtils.encryptAES256(
                newMiddleLevelSpringbootProject1Freelancer.uid.toString(),
                "AES/CBC/PKCS5Padding",
                ProjectConfigs.SERVER_SECRET_IV,
                ProjectConfigs.SERVER_SECRET_SECRET_KEY
        );

        httpServletResponse.setStatus(HttpStatus.OK.value());
        return new C2FreelancerController.Api1InsertFreelancerOutputVo(encodedUid);
    }


    ////
    // (등록된 프리렌서 페이징 리스트 반환 함수)
    public C2FreelancerController.Api2SelectFreelancersPageOutputVo api2SelectFreelancersPage(
            HttpServletResponse httpServletResponse,
            int page,
            int pageElementsCount,
            MiddleLevelSpringbootProject1_RepositoryDsl.FindFreelancersWithPaginationSortingType sortingType
    ) {
        // QueryDSL 프리렌서 페이징 정보 가져오기
        MiddleLevelSpringbootProject1_RepositoryDslImpl.FindFreelancersWithPaginationResult findFreelancersWithPaginationResultVoPage =
                middleLevelSpringbootProject1RepositoryDsl.findFreelancersWithPagination(page, pageElementsCount, sortingType);

        // 페이징 정보 결과값에 매핑
        ArrayList<C2FreelancerController.Api2SelectFreelancersPageOutputVo.Api2SelectFreelancersPageOutputVoFreelancer> freelancerList = new ArrayList<>();
        for (MiddleLevelSpringbootProject1_RepositoryDslImpl.FindFreelancersWithPaginationResult.FindFreelancersWithPaginationResultVo findFreelancersWithPaginationResultVo : findFreelancersWithPaginationResultVoPage.freelancers()) {
            // 등록된 프리렌서 고유값 암호화
            String encodedUid = cryptoUtils.encryptAES256(
                    findFreelancersWithPaginationResultVo.uid.toString(),
                    "AES/CBC/PKCS5Padding",
                    ProjectConfigs.SERVER_SECRET_IV,
                    ProjectConfigs.SERVER_SECRET_SECRET_KEY
            );

            freelancerList.add(
                    new C2FreelancerController.Api2SelectFreelancersPageOutputVo.Api2SelectFreelancersPageOutputVoFreelancer(
                            encodedUid,
                            findFreelancersWithPaginationResultVo.name,
                            (findFreelancersWithPaginationResultVo.viewCount == null) ? 0 : findFreelancersWithPaginationResultVo.viewCount,
                            findFreelancersWithPaginationResultVo.rowCreateDate.atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                    )
            );
        }

        httpServletResponse.setStatus(HttpStatus.OK.value());
        return new C2FreelancerController.Api2SelectFreelancersPageOutputVo(findFreelancersWithPaginationResultVoPage.totalCount(), freelancerList);
    }


    ////
    // (프리렌서 정보 카운트 up 함수)
    // 대량의 요청이 몰릴 것으로 예상되는 업데이트 함수 = 비동기 처리 필요
    @Transactional
    public void api3Plus1FreelancerView(
            HttpServletResponse httpServletResponse,
            C2FreelancerController.api3Plus1FreelancerViewInputVo inputVo
    ) {
        // 별도 스레드풀에서 처리
        viewCountUpThreadPool.submit(() -> processFreelancerView(inputVo.freelancerUid()));

        // 응답이 필요한 종류의 api 가 아니므로 응답을 바로 반환
        httpServletResponse.setStatus(HttpStatus.OK.value());
    }

    // (ViewCount 1up 작업 함수)
    // 1up 은 처리가 늦어져도 상관 없고, 처리만 된다면 상관 없기에 함수 자체에 접근 락을 걸어 처리
    private void processFreelancerView(String freelancerUid) {
        try {
            // 접근 락
            viewCountUpSemaphore.acquire();

            // 받은 프리렌서 고유값 복호화
            String decodedUid = cryptoUtils.decryptAES256(
                    freelancerUid,
                    "AES/CBC/PKCS5Padding",
                    ProjectConfigs.SERVER_SECRET_IV,
                    ProjectConfigs.SERVER_SECRET_SECRET_KEY
            );

            // String 타입 uid 파라미터를 Long 으로 변경
            long freelancerUidLong = Long.parseLong(decodedUid);

            // Freelancer 정보를 찾아옵니다.
            Optional<MiddleLevelSpringbootProject1_Freelancer> freelancerOptional =
                    middleLevelSpringbootProject1FreelancerRepository.findByUidAndRowDeleteDateStr(freelancerUidLong, "/");

            if (freelancerOptional.isEmpty()) {
                // 존재하지 않을 경우 로그 남기기
                return;
            }

            MiddleLevelSpringbootProject1_Freelancer freelancer = freelancerOptional.get();

            // FreelancerView 정보를 찾아옵니다.
            Optional<MiddleLevelSpringbootProject1_FreelancerView> freelancerViewOpt =
                    middleLevelSpringbootProject1FreelancerViewRepository.findByFreelancerAndRowDeleteDateStr(freelancer, "/");

            MiddleLevelSpringbootProject1_FreelancerView freelancerView;
            if (freelancerViewOpt.isEmpty()) {
                // freelancerView 가 존재하지 않음
                // 기존 freelancerView 생성
                freelancerView = new MiddleLevelSpringbootProject1_FreelancerView(1L, freelancer);
            } else {
                // freelancerView 가 존재함
                // 기존 freelancerView viewCount up
                freelancerView = freelancerViewOpt.get();
                freelancerView.viewCount += 1;
            }
            middleLevelSpringbootProject1FreelancerViewRepository.save(freelancerView);
        } catch (Exception e) {
            classLogger.error(e.toString());
            // 트랜젝션 롤백 발동을 위한 RuntimeException
            throw new RuntimeException("processFreelancerView block error");
        } finally {
            viewCountUpSemaphore.release();  // 락 해제
        }
    }
}
