package com.raillylinker.module_api_service_v1.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raillylinker.module_api_service_v1.services.C3PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Point APIs", description = "C3 : 서비스 Point 기능 구현 API 컨트롤러")
@Controller
@RequestMapping("/point")
public class C3PointController {
    // <멤버 변수 공간>
    // 생성자 주입
    public C3PointController(C3PointService service) {
        this.service = service;
    }

    @Autowired
    private final C3PointService service;


    // ---------------------------------------------------------------------------------------------
    // <매핑 함수 공간>
    // (토스 페이 결제 API 구현)
    // 만약 PG 사 변경 등 결제 방식이 바뀐다면 결제 로직, 입력값 형태 등이 바뀔 것이고,
    // 클라이언트 역시 배포를 거쳐야 하기에 결제 방식별 API 를 따로 만들었습니다.
    // 새로운 결제 방식 도입시 데이터베이스 및 서버 내부 로직을 따르는 방식으로 인터페이스를 연결하여 처리.
    @Operation(
            summary = "N1 : 포인트 결제(토스페이) API",
            description = "토스 페이를 사용하여 서비스 포인트를 결제 처리합니다.\n\n"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "정상 동작"
                    )
            }
    )
    @PostMapping(
            path = {"toss-pay-service-point"},
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public Api1TossPayServicePointOutputVo api1TossPayServicePoint(
            @Parameter(hidden = true) HttpServletResponse httpServletResponse,
            @Valid @NotNull @RequestHeader
            @Parameter(name = "freelancerUid", description = "결제할 프리랜서 등록 고유번호(암호화) - 인증/인가 토큰을 대신함", example = "1")
            String freelancerUid,
            @Valid @NotNull @RequestBody
            Api1TossPayServicePointInputVo inputVo
    ) {
        return service.api1TossPayServicePoint(httpServletResponse, freelancerUid, inputVo);
    }

    public record Api1TossPayServicePointInputVo(
            @Schema(
                    description = "토스 인가 코드",
                    example = "tossPayCode"
            )
            @JsonProperty("paymentKey")
            @Valid @NotNull
            String paymentKey,
            @Schema(
                    description = "토스 주문 번호",
                    example = "tossOrderId"
            )
            @JsonProperty("orderId")
            @Valid @NotNull
            String orderId,
            @Schema(
                    description = "결제 금액",
                    example = "orderAmount"
            )
            @JsonProperty("orderAmount")
            @Valid @NotNull
            Long orderAmount
    ) {
    }

    public record Api1TossPayServicePointOutputVo(
            @Schema(
                    description = "결과 코드\n\n" +
                            "1 : 토스 페이 결제 요청 성공\n\n" +
                            "2 : 토스 페이 결제 요청 실패\n\n" +
                            "3 : 토스 페이 결제 요청 네트워크 오류",
                    example = "1"
            )
            @JsonProperty("resultCode")
            @Valid @NotNull
            int resultCode
    ) {
    }
}
