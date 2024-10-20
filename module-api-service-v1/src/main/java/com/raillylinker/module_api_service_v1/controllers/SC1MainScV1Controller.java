package com.raillylinker.module_api_service_v1.controllers;

import com.raillylinker.module_api_service_v1.services.SC1MainScV1Service;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Hidden
@Tag(name = "/main/sc/v1 APIs", description = "SC1 : main 웹 페이지에 대한 API 컨트롤러")
@Controller
@RequestMapping("/main/sc/v1")
public class SC1MainScV1Controller {
    // <멤버 변수 공간>
    // 생성자 주입
    public SC1MainScV1Controller(SC1MainScV1Service service) {
        this.service = service;
    }

    private final SC1MainScV1Service service;


    // ---------------------------------------------------------------------------------------------
    // <매핑 함수 공간>
    @Operation(summary = "N1 : 홈페이지", description = "루트 홈페이지를 반환합니다.\n\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 동작")
    })
    @GetMapping(path = {"/home"}, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView api1GetRoot(
            @Parameter(hidden = true) HttpServletRequest httpServletRequest,
            @Parameter(hidden = true) HttpServletResponse httpServletResponse,
            @Parameter(hidden = true) HttpSession session
    ) {
        return service.api1HomePage(httpServletRequest, httpServletResponse, session);
    }
}