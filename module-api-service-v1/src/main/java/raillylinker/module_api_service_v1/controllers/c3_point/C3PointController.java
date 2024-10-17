package raillylinker.module_api_service_v1.controllers.c3_point;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Tag(name = "Point APIs", description = "C3 : Point 기능 구현 API 컨트롤러")
@Controller
@RequestMapping("/point")
public class C3PointController {
    // <멤버 변수 공간>
    // 생성자 주입
    public C3PointController(C3PointService service) {
        this.service = service;
    }

    private final C3PointService service;


    // ---------------------------------------------------------------------------------------------
    // <매핑 함수 공간>
//    @Operation(summary = "N1 : 홈페이지", description = "루트 홈페이지를 반환합니다.\n\n")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "정상 동작")
//    })
//    @GetMapping(path = {"", "/"}, consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_HTML_VALUE)
//    public ModelAndView api1GetRoot(@Parameter(hidden = true) HttpServletResponse httpServletResponse) {
//        return service.api1GetRoot(httpServletResponse);
//    }
}
