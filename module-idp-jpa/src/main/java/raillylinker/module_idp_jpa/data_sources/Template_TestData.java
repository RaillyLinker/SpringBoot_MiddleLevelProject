package raillylinker.module_idp_jpa.data_sources;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "test_data",
        catalog = "template"
)
@Comment("테스트 정보 테이블(논리적 삭제 적용)")
public class Template_TestData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    @Comment("행 고유값")
    public Long uid;

    @Column(name = "row_create_date", nullable = false, columnDefinition = "DATETIME(3)")
    @CreationTimestamp
    @Comment("행 생성일")
    public LocalDateTime rowCreateDate;

    @Column(name = "row_update_date", nullable = false, columnDefinition = "DATETIME(3)")
    @UpdateTimestamp
    @Comment("행 수정일")
    public LocalDateTime rowUpdateDate;

    @Column(name = "content", nullable = false, columnDefinition = "VARCHAR(255)")
    @Comment("테스트 본문")
    public String content;

    @Column(name = "random_num", nullable = false, columnDefinition = "INT")
    @Comment("테스트 랜덤 번호")
    public int randomNum;

    @Column(name = "test_datetime", nullable = false, columnDefinition = "DATETIME(3)")
    @Comment("테스트용 일시 데이터")
    public LocalDateTime testDatetime;

    @Column(name = "row_delete_date_str", nullable = false, columnDefinition = "VARCHAR(50)")
    @ColumnDefault("'/'")
    @Comment("행 삭제일(yyyy_MM_dd_T_HH_mm_ss_SSS_z, 삭제되지 않았다면 /)")
    public String rowDeleteDateStr = "/";
}
