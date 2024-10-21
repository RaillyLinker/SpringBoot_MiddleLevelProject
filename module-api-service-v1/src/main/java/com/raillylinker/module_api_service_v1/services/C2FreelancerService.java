package com.raillylinker.module_api_service_v1.services;

import com.raillylinker.module_api_service_v1.controllers.C2FreelancerController;
import com.raillylinker.module_idp_jpa.jpa_beans.repositories_dsl.MiddleLevelSpringbootProject1_RepositoryDsl;
import jakarta.servlet.http.HttpServletResponse;

public interface C2FreelancerService {
    // (프리랜서 등록 함수)
    C2FreelancerController.Api1InsertFreelancerOutputVo api1InsertFreelancer(
            HttpServletResponse httpServletResponse,
            C2FreelancerController.Api1InsertFreelancerInputVo inputVo
    );


    ////
    // (등록된 프리렌서 페이징 리스트 반환 함수)
    C2FreelancerController.Api2SelectFreelancersPageOutputVo api2SelectFreelancersPage(
            HttpServletResponse httpServletResponse,
            int page,
            int pageElementsCount,
            MiddleLevelSpringbootProject1_RepositoryDsl.FindFreelancersWithPaginationSortingType sortingType
    );


    ////
    // (프리렌서 정보 카운트 up 함수)
    void api3Plus1FreelancerView(
            HttpServletResponse httpServletResponse,
            C2FreelancerController.api3Plus1FreelancerViewInputVo inputVo
    );
}
