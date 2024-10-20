package com.raillylinker.module_idp_jpa.jpa.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "service_point_payment_toss_pay_info",
        catalog = "middle_level_springboot_project1",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"service_point_payment_history_uid", "row_delete_date_str"})
        }
)
@Comment("서비스 포인트 토스 페이 결제 정보 테이블")
public class MiddleLevelSpringbootProject1_ServicePointPaymentTossPayInfo {
    public MiddleLevelSpringbootProject1_ServicePointPaymentTossPayInfo() {
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
    public MiddleLevelSpringbootProject1_ServicePointPaymentTossPayInfo(
            String paymentKey,
            String orderId,
            MiddleLevelSpringbootProject1_ServicePointPaymentHistory servicePointPaymentHistory
    ) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.servicePointPaymentHistory = servicePointPaymentHistory;

    }

    @Column(name = "payment_key", nullable = false, columnDefinition = "VARCHAR(50)")
    @Comment("toss paymentKey")
    public String paymentKey;

    @Column(name = "order_id", nullable = false, columnDefinition = "VARCHAR(50)")
    @Comment("toss orderId")
    public String orderId;

    @OneToOne
    @JoinColumn(name = "service_point_payment_history_uid", nullable = false)
    @Comment("서비스 포인트 결제 히스트리 고유번호(middle_level_springboot_project1.service_point_payment_history.uid)")
    public MiddleLevelSpringbootProject1_ServicePointPaymentHistory servicePointPaymentHistory;
}
