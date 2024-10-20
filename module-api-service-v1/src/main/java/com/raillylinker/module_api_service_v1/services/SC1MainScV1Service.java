package com.raillylinker.module_api_service_v1.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.ModelAndView;

public interface SC1MainScV1Service {
    // (홈페이지 반환 함수)
    ModelAndView api1HomePage(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            HttpSession session
    );
}