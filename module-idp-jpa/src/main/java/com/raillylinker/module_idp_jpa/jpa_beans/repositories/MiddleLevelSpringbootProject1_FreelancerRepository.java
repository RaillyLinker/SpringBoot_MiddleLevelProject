package com.raillylinker.module_idp_jpa.jpa_beans.repositories;

import com.raillylinker.module_idp_jpa.jpa_beans.entities.MiddleLevelSpringbootProject1_Freelancer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MiddleLevelSpringbootProject1_FreelancerRepository extends JpaRepository<MiddleLevelSpringbootProject1_Freelancer, Long> {
    Optional<MiddleLevelSpringbootProject1_Freelancer> findByUidAndRowDeleteDateStr(Long uid, String rowDeleteDateStr);
}
