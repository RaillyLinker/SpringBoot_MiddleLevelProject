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
import raillylinker.module_idp_jpa.data_sources.MiddleLevelSpringbootProject1_Freelancer;
import raillylinker.module_idp_jpa.data_sources.MiddleLevelSpringbootProject1_FreelancerRepository;
import raillylinker.module_idp_jpa.data_sources.MiddleLevelSpringbootProject1_FreelancerViewRepository;
import raillylinker.module_idp_jpa.data_sources.MiddleLevelSpringbootProject1_RepositoryDsl;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
        MiddleLevelSpringbootProject1_RepositoryDsl.FindFreelancersWithPaginationResult findFreelancersWithPaginationResultVoPage =
                middleLevelSpringbootProject1RepositoryDsl.findFreelancersWithPagination(page, pageElementsCount, sortingType);

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
    public void api3Plus1FreelancerView(
            HttpServletResponse httpServletResponse,
            C2FreelancerController.api3Plus1FreelancerViewInputVo inputVo
    ) {
        // todo
        httpServletResponse.setStatus(HttpStatus.OK.value());
    }
}
