package com.raillylinker.module_api_service_v1.services.Impl;

import com.raillylinker.module_api_service_v1.services.SC1MainScV1Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service
public class SC1MainScV1ServiceImpl implements SC1MainScV1Service {
    // <멤버 변수 공간>
    // 프로젝트 실행 시 사용 설정한 프로필명 (설정 안하면 default 반환)
    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    // (스웨거 문서 공개 여부 설정)
    @Value("${springdoc.swagger-ui.enabled}")
    private boolean swaggerEnabled;

    private final Logger classLogger = LoggerFactory.getLogger(this.getClass());


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    public ModelAndView api1HomePage(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            HttpSession session
    ) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("for_sc1_n1_home_page/home_page");

        mv.addObject("viewModel", new Api1ViewModel(activeProfile, swaggerEnabled));

        return mv;
    }

    public record Api1ViewModel(String env, boolean showApiDocumentBtn) {
    }
}