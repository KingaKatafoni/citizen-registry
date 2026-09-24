package pl.gov.eurzad.citizenregistry.officer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.gov.eurzad.citizenregistry.officer.model.Officer;

public interface OfficerRepository extends JpaRepository<Officer, Long> {

}
