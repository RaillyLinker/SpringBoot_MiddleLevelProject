package com.raillylinker.module_api_service_v1.services;

import com.raillylinker.module_api_service_v1.controllers.C3PointController;
import jakarta.servlet.http.HttpServletResponse;

public interface C3PointService {
    // (포인트 결제(토스페이) API 구현 함수)
    C3PointController.Api1TossPayServicePointOutputVo api1TossPayServicePoint(
            HttpServletResponse httpServletResponse,
            String freelancerUid,
            C3PointController.Api1TossPayServicePointInputVo inputVo
    );
}
