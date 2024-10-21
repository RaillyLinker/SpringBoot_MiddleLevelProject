package com.raillylinker.module_idp_jpa.jpa_beans.repositories;

import com.raillylinker.module_idp_jpa.jpa_beans.entities.MiddleLevelSpringbootProject1_Freelancer;
import com.raillylinker.module_idp_jpa.jpa_beans.entities.MiddleLevelSpringbootProject1_ServicePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MiddleLevelSpringbootProject1_ServicePointRepository extends JpaRepository<MiddleLevelSpringbootProject1_ServicePoint, Long> {
    Optional<MiddleLevelSpringbootProject1_ServicePoint> findByFreelancerAndRowDeleteDateStr(MiddleLevelSpringbootProject1_Freelancer freelancer, String rowDeleteDateStr);
}
