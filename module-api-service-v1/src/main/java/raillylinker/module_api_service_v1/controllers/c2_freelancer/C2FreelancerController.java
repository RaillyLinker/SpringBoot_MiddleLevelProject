package raillylinker.module_api_service_v1.controllers.c2_freelancer;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.*;
import raillylinker.module_idp_jpa.data_sources.MiddleLevelSpringbootProject1_RepositoryDsl;

import java.util.List;

@Tag(name = "Freelancer APIs", description = "C2 : Freelancer 기능 구현 API 컨트롤러")
@Controller
@RequestMapping("/freelancer")
public class C2FreelancerController {
    // <멤버 변수 공간>
    // 생성자 주입
    public C2FreelancerController(C2FreelancerService service) {
        this.service = service;
    }

    private final C2FreelancerService service;


    // ---------------------------------------------------------------------------------------------
    // <매핑 함수 공간>
    @Operation(
            summary = "N1 : 프리랜서 등록용 API",
            description = "테스트를 위하여 프리렌서 정보를 등록합니다.\n\n"
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
            path = {"freelancer"},
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public Api1InsertFreelancerOutputVo api1InsertFreelancer(
            @Parameter(hidden = true) HttpServletResponse httpServletResponse,
            @Valid @RequestBody
            Api1InsertFreelancerInputVo inputVo
    ) {
        return service.api1InsertFreelancer(httpServletResponse, inputVo);
    }

    public record Api1InsertFreelancerInputVo(
            @Schema(
                    description = "프리랜서 이름",
                    example = "홍길동"
            )
            @JsonProperty("freelancerName")
            @Valid @NotNull
            String freelancerName) {
    }

    public record Api1InsertFreelancerOutputVo(
            @Schema(
                    description = "프리랜서 등록 고유번호(암호화)",
                    example = "1"
            )
            @JsonProperty("freelancerUid")
            @Valid @NotNull
            String freelancerUid
    ) {
    }


    ////
    @Operation(
            summary = "N2 : 프리랜서 목록 조회 API",
            description = "현재 등록된 프리랜서 목록을 페이지로 조회합니다.\n\n"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "정상 동작"
                    )
            }
    )
    @GetMapping(
            path = {"freelancers"},
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public Api2SelectFreelancersPageOutputVo api2SelectFreelancersPage(
            @Parameter(hidden = true) HttpServletResponse httpServletResponse,
            @Parameter(name = "page", description = "원하는 페이지(1 부터 시작)", example = "1")
            @Valid @NotNull @RequestParam("page") int page,
            @Parameter(name = "pageElementsCount", description = "페이지 아이템 개수", example = "10")
            @Valid @NotNull @RequestParam("pageElementsCount") int pageElementsCount,
            @Parameter(
                    name = "sortingType",
                    description = "정렬 기준\n\n" +
                            "NAME_ASC : 이름 오름차순\n\n" +
                            "NAME_DESC : 이름 내림차순\n\n" +
                            "VIEW_ASC : 조회수 오름차순\n\n" +
                            "VIEW_DESC : 조회수 내림차순\n\n" +
                            "CREATE_ASC : 등록일 오름차순\n\n" +
                            "CREATE_DESC : 등록일 내림차순\n\n",
                    example = "NAME_ASC")
            @Valid @NotNull @RequestParam("sortingType") MiddleLevelSpringbootProject1_RepositoryDsl.Api2SelectFreelancersPageSortingType sortingType
    ) {
        return service.api2SelectFreelancersPage(httpServletResponse, page, pageElementsCount, sortingType);
    }

    public record Api2SelectFreelancersPageOutputVo(
            @Schema(
                    description = "프리랜서 정보 총 개수",
                    example = "1"
            )
            @JsonProperty("freelancerListTotalCount")
            @Valid @NotNull
            Long freelancerListTotalCount,
            @Schema(
                    description = "프리랜서 리스트"
            )
            @JsonProperty("freelancerList")
            @Valid @NotNull
            List<Api2SelectFreelancersPageOutputVoFreelancer> freelancerList
    ) {
        @Schema(
                description = "프리랜서 정보 클래스"
        )
        public record Api2SelectFreelancersPageOutputVoFreelancer(
                @Schema(
                        description = "프리랜서 등록 고유번호(암호화)",
                        example = "1"
                )
                @JsonProperty("freelancerUid")
                @Valid @NotNull
                String freelancerUid,
                @Schema(
                        description = "프리랜서 이름",
                        example = "1"
                )
                @JsonProperty("freelancerName")
                @Valid @NotNull
                String freelancerName,
                @Schema(
                        description = "프리랜서 정보 조회수",
                        example = "1"
                )
                @JsonProperty("freelancerViewCount")
                @Valid @NotNull
                Long freelancerViewCount,
                @Schema(
                        description = "프리랜서 등록일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                        example = "2024_05_02_T_15_14_49_552_KST"
                )
                @JsonProperty("freelancerCreateDate")
                @Valid @NotNull
                String freelancerCreateDate
        ) {
        }
    }


    ////
    @Operation(
            summary = "N3 : 프리랜서 조회수 업데이트 API",
            description = "프리랜서 조회수를 업데이트 합니다.\n\n"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "정상 동작"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "리소스가 존재하지 않습니다."
                    )
            }
    )
    @PatchMapping(
            path = {"freelancer-view"},
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public void api3Plus1FreelancerView(
            @Parameter(hidden = true) HttpServletResponse httpServletResponse,
            @Valid @RequestBody
            api3Plus1FreelancerViewInputVo inputVo
    ) {
        service.api3Plus1FreelancerView(httpServletResponse, inputVo);
    }

    public record api3Plus1FreelancerViewInputVo(
            @Schema(
                    description = "프리랜서 등록 고유번호(암호화)",
                    example = "1"
            )
            @JsonProperty("freelancerUid")
            @Valid @NotNull
            String freelancerUid) {
    }
}
