package raillylinker.module_idp_jpa.data_sources;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MiddleLevelSpringbootProject1_FreelancerViewRepository extends JpaRepository<MiddleLevelSpringbootProject1_FreelancerView, Long> {
    Optional<MiddleLevelSpringbootProject1_FreelancerView> findByFreelancerAndRowDeleteDateStr(MiddleLevelSpringbootProject1_Freelancer freelancer, String rowDeleteDateStr);
}
