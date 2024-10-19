package raillylinker.module_idp_jpa.data_sources.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "freelancer",
        catalog = "middle_level_springboot_project1"
)
@Comment("프리렌서 테이블")
public class MiddleLevelSpringbootProject1_Freelancer {
    public MiddleLevelSpringbootProject1_Freelancer() {
    }

    // [기본 입력값이 존재하는 변수들]
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

    @Column(name = "row_delete_date_str", nullable = false, columnDefinition = "VARCHAR(50)")
    @ColumnDefault("'/'")
    @Comment("행 삭제일(yyyy_MM_dd_T_HH_mm_ss_SSS_z, 삭제되지 않았다면 /)")
    public String rowDeleteDateStr = "/";


    // ---------------------------------------------------------------------------------------------
    // [입력값 수동 입력 변수들]
    public MiddleLevelSpringbootProject1_Freelancer(
            String name
    ) {
        this.name = name;
    }

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(80)")
    @Comment("이름")
    public String name;


    // ---------------------------------------------------------------------------------------------
    // [@OneToMany 변수들]
    @OneToMany(
            mappedBy = "freelancer",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL}
    )
    public List<MiddleLevelSpringbootProject1_ServicePointPaymentHistory> servicePointHistoryList;
}
