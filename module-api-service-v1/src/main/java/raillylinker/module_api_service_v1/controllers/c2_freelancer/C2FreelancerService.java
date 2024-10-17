package raillylinker.module_api_service_v1.controllers.c2_freelancer;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import raillylinker.module_idp_common.custom_objects.CustomUtils;

@Service
public class C2FreelancerService {
    // <멤버 변수 공간>
    // 프로젝트 실행 시 사용 설정한 프로필명 (설정 안하면 default 반환)
    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    private final Logger classLogger = LoggerFactory.getLogger(this.getClass());


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (프리랜서 등록 함수)
    public C2FreelancerController.Api1InsertFreelancerOutputVo api1InsertFreelancer(HttpServletResponse httpServletResponse) {
        // todo
        CustomUtils.test();
        return new C2FreelancerController.Api1InsertFreelancerOutputVo("test");
    }

    public C2FreelancerController.Api2SelectFreelancersPageOutputVo api2SelectFreelancersPage(HttpServletResponse httpServletResponse) {
        // todo
        return new C2FreelancerController.Api2SelectFreelancersPageOutputVo("test");
    }

    public void api3Plus1FreelancerView(HttpServletResponse httpServletResponse) {
        // todo
    }
}
