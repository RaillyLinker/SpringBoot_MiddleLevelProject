package raillylinker.module_api_service_v1.controllers.c2_freelancer;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raillylinker.module_api_service_v1.datasources.memory_const_object.ProjectConst;
import raillylinker.module_idp_common.custom_classes.CryptoUtils;
import raillylinker.module_idp_jpa.data_sources.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class C2FreelancerService {
    // <멤버 변수 공간>
    // 프로젝트 실행 시 사용 설정한 프로필명 (설정 안하면 default 반환)
    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    private final Logger classLogger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MiddleLevelSpringbootProject1_FreelancerRepository middleLevelSpringbootProject1FreelancerRepository;

    @Autowired
    MiddleLevelSpringbootProject1_FreelancerViewRepository middleLevelSpringbootProject1FreelancerViewRepository;

    @Autowired
    MiddleLevelSpringbootProject1_RepositoryDsl middleLevelSpringbootProject1RepositoryDsl;

    // (viewCount up 작업용 스레드풀)
    // 메시지 큐를 이용한 작업 분산 처리를 대신하여 사용하였습니다.
    // (Kotlin 에서 구현했던 Kafka 설정을 Java 로 옮길 때 시간이 걸릴 것을 고려)
    // 기능 설계 : 작업 발생시 메시지 큐로 이벤트 발송(viewCount up 할 freelancerView uid 전송)
    //     -> 이벤트를 받은 노드에서 분산락 획득 후 freelancerView uid 에서 카운트 up
    ExecutorService viewCountUpThreadPool = Executors.newFixedThreadPool(5);


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (프리랜서 등록 함수)
    @Transactional
    public C2FreelancerController.Api1InsertFreelancerOutputVo api1InsertFreelancer(
            HttpServletResponse httpServletResponse,
            C2FreelancerController.Api1InsertFreelancerInputVo inputVo
    ) {
        // 프리렌서 입력 정보
        MiddleLevelSpringbootProject1_Freelancer middleLevelSpringbootProject1Freelancer = new MiddleLevelSpringbootProject1_Freelancer(inputVo.freelancerName());

        // 프리렌서 정보 등록
        MiddleLevelSpringbootProject1_Freelancer newMiddleLevelSpringbootProject1Freelancer =
                middleLevelSpringbootProject1FreelancerRepository.save(middleLevelSpringbootProject1Freelancer);

        // @Transactional 롤백 테스트 (완료)
//        throw new RuntimeException("강제 예외 발생으로 인해 롤백되어야 함.");

        // 등록된 프리렌서 고유값 암호화
        String encodedUid = CryptoUtils.encryptAES256(
                newMiddleLevelSpringbootProject1Freelancer.uid.toString(),
                "AES/CBC/PKCS5Padding",
                ProjectConst.SERVER_SECRET_IV,
                ProjectConst.SERVER_SECRET_SECRET_KEY
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
            MiddleLevelSpringbootProject1_RepositoryDsl.Api2SelectFreelancersPageSortingType sortingType
    ) {
        // QueryDSL 프리렌서 페이징 정보 가져오기
        MiddleLevelSpringbootProject1_RepositoryDsl.FindFreelancersWithPaginationResult findFreelancersWithPaginationResultVoPage =
                middleLevelSpringbootProject1RepositoryDsl.findFreelancersWithPagination(page, pageElementsCount, sortingType);

        // 페이징 정보 결과값에 매핑
        ArrayList<C2FreelancerController.Api2SelectFreelancersPageOutputVo.Api2SelectFreelancersPageOutputVoFreelancer> freelancerList = new ArrayList<>();
        for (MiddleLevelSpringbootProject1_RepositoryDsl.FindFreelancersWithPaginationResult.FindFreelancersWithPaginationResultVo findFreelancersWithPaginationResultVo : findFreelancersWithPaginationResultVoPage.freelancers()) {
            // 등록된 프리렌서 고유값 암호화
            String encodedUid = CryptoUtils.encryptAES256(
                    findFreelancersWithPaginationResultVo.uid.toString(),
                    "AES/CBC/PKCS5Padding",
                    ProjectConst.SERVER_SECRET_IV,
                    ProjectConst.SERVER_SECRET_SECRET_KEY
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
    // todo Redis 를 이용한 분산락 구현하기
    @Transactional
    public void api3Plus1FreelancerView(
            HttpServletResponse httpServletResponse,
            C2FreelancerController.api3Plus1FreelancerViewInputVo inputVo
    ) {
        // 별도 스레드풀에서 처리
        viewCountUpThreadPool.submit(() -> processFreelancerView(inputVo.freelancerUid()));

        // 응답을 바로 반환
        httpServletResponse.setStatus(HttpStatus.OK.value());
    }

    // (ViewCount 1up 작업 함수)
    private void processFreelancerView(String freelancerUid) {
        long freelancerUidLong;
        try {
            // 받은 프리렌서 고유값 복호화
            String decodedUid = CryptoUtils.decryptAES256(
                    freelancerUid,
                    "AES/CBC/PKCS5Padding",
                    ProjectConst.SERVER_SECRET_IV,
                    ProjectConst.SERVER_SECRET_SECRET_KEY
            );

            // String 타입 uid 파라미터를 Long 으로 변경
            freelancerUidLong = Long.parseLong(decodedUid);
        } catch (Exception e) {
            // 에러 발생시 로그 남기기
            return;
        }

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
    }
}
