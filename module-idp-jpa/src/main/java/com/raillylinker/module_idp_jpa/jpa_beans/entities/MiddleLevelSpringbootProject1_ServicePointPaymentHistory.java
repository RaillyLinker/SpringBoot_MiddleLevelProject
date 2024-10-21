package com.raillylinker.module_idp_jpa.jpa_beans.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "service_point_payment_history",
        catalog = "middle_level_springboot_project1"
)
@Comment("서비스 포인트 보유 현황 테이블")
public class MiddleLevelSpringbootProject1_ServicePointPaymentHistory {
    public MiddleLevelSpringbootProject1_ServicePointPaymentHistory() {
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
    public MiddleLevelSpringbootProject1_ServicePointPaymentHistory(
            PaymentType paymentType,
            Long paymentPrice,
            double plusPoint,
            MiddleLevelSpringbootProject1_Freelancer freelancer
    ) {
        this.paymentType = paymentType;
        this.paymentPrice = paymentPrice;
        this.plusPoint = plusPoint;
        this.freelancer = freelancer;

    }

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, columnDefinition = "VARCHAR(50)")
    @Comment("결제 종류")
    public PaymentType paymentType;

    @Column(name = "payment_price", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    @Comment("결제 금액")
    public Long paymentPrice;

    @Column(name = "plus_point", nullable = false, columnDefinition = "DOUBLE UNSIGNED")
    @Comment("추가된 포인트")
    public Double plusPoint;

    @ManyToOne
    @JoinColumn(name = "freelancer_uid", nullable = false)
    @Comment("서비스 포인트 보유자 프리랜서 테이블 고유번호(middle_level_springboot_project1.freelancer.uid)")
    public MiddleLevelSpringbootProject1_Freelancer freelancer;

    public enum PaymentType {
        TOSS_PAY
    }
}
