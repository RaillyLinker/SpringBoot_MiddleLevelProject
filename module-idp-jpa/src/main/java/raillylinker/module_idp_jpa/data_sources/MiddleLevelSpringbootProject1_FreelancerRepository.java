package raillylinker.module_idp_jpa.data_sources;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MiddleLevelSpringbootProject1_FreelancerRepository extends JpaRepository<MiddleLevelSpringbootProject1_Freelancer, Long> {
    Optional<MiddleLevelSpringbootProject1_Freelancer> findByUidAndRowDeleteDateStr(Long uid, String rowDeleteDateStr);
}
