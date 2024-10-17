package raillylinker.module_api_service_v1.controllers.c2_freelancer;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import raillylinker.module_api_service_v1.datasources.memory_const_object.ProjectConst;
import raillylinker.module_idp_common.custom_classes.CryptoUtils;
import raillylinker.module_idp_jpa.data_sources.MiddleLevelSpringbootProject1_Freelancer;
import raillylinker.module_idp_jpa.data_sources.MiddleLevelSpringbootProject1_FreelancerRepository;
import raillylinker.module_idp_jpa.data_sources.MiddleLevelSpringbootProject1_FreelancerViewRepository;

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


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (프리랜서 등록 함수)
    @Transactional
    public C2FreelancerController.Api1InsertFreelancerOutputVo api1InsertFreelancer(
            HttpServletResponse httpServletResponse,
            C2FreelancerController.Api1InsertFreelancerInputVo inputVo
    ) {
        // 프리렌서 입력 정보
        MiddleLevelSpringbootProject1_Freelancer middleLevelSpringbootProject1Freelancer = new MiddleLevelSpringbootProject1_Freelancer();
        middleLevelSpringbootProject1Freelancer.name = inputVo.freelancerName();

        // 프리렌서 정보 등록
        MiddleLevelSpringbootProject1_Freelancer newMiddleLevelSpringbootProject1Freelancer =
                middleLevelSpringbootProject1FreelancerRepository.save(middleLevelSpringbootProject1Freelancer);

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

    // (등록된 프리렌서 페이징 리스트 반환 함수)
    public C2FreelancerController.Api2SelectFreelancersPageOutputVo api2SelectFreelancersPage(
            HttpServletResponse httpServletResponse,
            int page,
            int pageElementsCount,
            C2FreelancerController.Api2SelectFreelancersPageSortingType sortingType) {
        // todo

        httpServletResponse.setStatus(HttpStatus.OK.value());
        return new C2FreelancerController.Api2SelectFreelancersPageOutputVo("test");
    }

    // (프리렌서 정보 카운트 up 함수)
    public void api3Plus1FreelancerView(
            HttpServletResponse httpServletResponse,
            C2FreelancerController.api3Plus1FreelancerViewInputVo inputVo
    ) {
        // todo
        httpServletResponse.setStatus(HttpStatus.OK.value());
    }
}
