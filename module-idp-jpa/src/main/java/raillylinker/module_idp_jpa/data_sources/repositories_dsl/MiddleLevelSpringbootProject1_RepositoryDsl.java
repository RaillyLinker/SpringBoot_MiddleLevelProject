package raillylinker.module_idp_jpa.data_sources.repositories_dsl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import raillylinker.module_idp_jpa.data_sources.entities.QMiddleLevelSpringbootProject1_Freelancer;
import raillylinker.module_idp_jpa.data_sources.entities.QMiddleLevelSpringbootProject1_FreelancerView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class MiddleLevelSpringbootProject1_RepositoryDsl {
    MiddleLevelSpringbootProject1_RepositoryDsl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    private final JPAQueryFactory jpaQueryFactory;


    // ---------------------------------------------------------------------------------------------
    // (Query DSL 함수 작성 공간)
    public FindFreelancersWithPaginationResult findFreelancersWithPagination(
            int page,
            int size,
            Api2SelectFreelancersPageSortingType sortingType
    ) {
        QMiddleLevelSpringbootProject1_Freelancer freelancer = QMiddleLevelSpringbootProject1_Freelancer.middleLevelSpringbootProject1_Freelancer;
        QMiddleLevelSpringbootProject1_FreelancerView freelancerView = QMiddleLevelSpringbootProject1_FreelancerView.middleLevelSpringbootProject1_FreelancerView;

        // 총 개수 쿼리
        long totalCount = Optional.ofNullable(jpaQueryFactory
                .select(freelancer.count())
                .from(freelancer)
                .leftJoin(freelancerView)
                .on(freelancerView.rowDeleteDateStr.eq("/").and(freelancer.uid.eq(freelancerView.freelancer.uid)))
                .where(freelancer.rowDeleteDateStr.eq("/"))
                .fetchOne()).orElse(0L); // 기본값 0 설정

        // Freelancer - FreelancerView 테이블 조인 쿼리
        JPAQuery<FindFreelancersWithPaginationResult.FindFreelancersWithPaginationResultVo> query = jpaQueryFactory
                .select(Projections.constructor(
                        FindFreelancersWithPaginationResult.FindFreelancersWithPaginationResultVo.class,  // DTO 클래스
                        freelancer.uid,
                        freelancer.name,
                        freelancer.rowCreateDate,
                        freelancer.rowUpdateDate,
                        freelancerView.viewCount
                ))
                .from(freelancer)
                // freelancerView 와 leftJoin
                .leftJoin(freelancerView)
                // 삭제 되지 않음 rowDeleteDateStr == '/' 이라는 조건을 기본으로 필터링
                .on(freelancerView.rowDeleteDateStr.eq("/").and(freelancer.uid.eq(freelancerView.freelancer.uid)))
                .where(freelancer.rowDeleteDateStr.eq("/"))
                // 페이지 번호 계산
                .offset((long) (page - 1) * size)
                // 페이지당 아이템 수 제한
                .limit(size);

        // 정렬 조건 추가
        switch (sortingType) {
            case NAME_ASC:
                query.orderBy(freelancer.name.asc());
                break;
            case NAME_DESC:
                query.orderBy(freelancer.name.desc());
                break;
            case VIEW_ASC:
                query.orderBy(freelancerView.viewCount.asc());
                break;
            case VIEW_DESC:
                query.orderBy(freelancerView.viewCount.desc());
                break;
            case CREATE_ASC:
                query.orderBy(freelancer.rowCreateDate.asc());
                break;
            case CREATE_DESC:
                query.orderBy(freelancer.rowCreateDate.desc());
                break;
            default:
                throw new IllegalArgumentException("Invalid sorting type");
        }

        List<FindFreelancersWithPaginationResult.FindFreelancersWithPaginationResultVo> results = query.fetch();

        return new FindFreelancersWithPaginationResult(totalCount, results);
    }

    // findFreelancersWithPagination 함수 입력값 - 정렬 타입 Enum
    public enum Api2SelectFreelancersPageSortingType {
        NAME_ASC, // 이름 오름차순
        NAME_DESC, // 이름 내림차순
        VIEW_ASC, // 조회수 오름차순
        VIEW_DESC, // 조회수 내림차순
        CREATE_ASC, // 등록일 오름차순
        CREATE_DESC // 등록일 내림차순
    }

    // findFreelancersWithPagination 함수 반환값
    public record FindFreelancersWithPaginationResult(
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
