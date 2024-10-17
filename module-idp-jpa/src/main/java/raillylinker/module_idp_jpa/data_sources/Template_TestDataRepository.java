package raillylinker.module_idp_jpa.data_sources;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Template_TestDataRepository extends JpaRepository<Template_TestData, Long> {
}
