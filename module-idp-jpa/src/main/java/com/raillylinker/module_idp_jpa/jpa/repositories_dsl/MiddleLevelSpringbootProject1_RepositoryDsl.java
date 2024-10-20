package com.raillylinker.module_idp_jpa.jpa.repositories_dsl;

import java.time.LocalDateTime;
import java.util.List;

public interface MiddleLevelSpringbootProject1_RepositoryDsl {
    // (프리랜서 정보 페이징 반환 함수)
    FindFreelancersWithPaginationResult findFreelancersWithPagination(
            int page,
            int size,
            FindFreelancersWithPaginationSortingType sortingType
    );

    // findFreelancersWithPagination 함수 입력값 - 정렬 타입 Enum
    enum FindFreelancersWithPaginationSortingType {
        NAME_ASC, // 이름 오름차순
        NAME_DESC, // 이름 내림차순
        VIEW_ASC, // 조회수 오름차순
        VIEW_DESC, // 조회수 내림차순
        CREATE_ASC, // 등록일 오름차순
        CREATE_DESC // 등록일 내림차순
    }

    // findFreelancersWithPagination 함수 반환값
    record FindFreelancersWithPaginationResult(
            long totalCount,
            List<FindFreelancersWithPaginationResultVo> freelancers
    ) {
        public static class FindFreelancersWithPaginationResultVo {
            public FindFreelancersWithPaginationResultVo(Long uid, String name, LocalDateTime rowCreateDate, LocalDateTime rowUpdateDate, Long viewCount) {
                this.uid = uid;
                this.name = name;
                this.rowCreateDate = rowCreateDate;
                this.rowUpdateDate = rowUpdateDate;
                this.viewCount = viewCount;
            }

            public Long uid;
            public String name;
            public LocalDateTime rowCreateDate;
            public LocalDateTime rowUpdateDate;
            public Long viewCount;
        }
    }
}
