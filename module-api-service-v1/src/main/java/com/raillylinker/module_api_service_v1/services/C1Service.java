package com.raillylinker.module_api_service_v1.services;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

public interface C1Service {
    // (루트 경로 접속)
    ModelAndView api1GetRoot(HttpServletResponse httpServletResponse);
}