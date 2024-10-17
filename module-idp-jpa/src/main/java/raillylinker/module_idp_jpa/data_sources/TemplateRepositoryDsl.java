package raillylinker.module_idp_jpa.data_sources;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import static raillylinker.module_idp_jpa.data_sources.QTemplate_TestData.template_TestData;

@Repository
public class TemplateRepositoryDsl {
    TemplateRepositoryDsl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    private final JPAQueryFactory jpaQueryFactory;

    public Template_TestData findByUid(Long uid) {
        return
                jpaQueryFactory
                        .selectFrom(template_TestData)
                        .where(template_TestData.uid.eq(uid))
                        .fetchOne();
    }
}
