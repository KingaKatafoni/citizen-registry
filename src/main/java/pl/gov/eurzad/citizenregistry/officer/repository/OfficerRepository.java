package pl.gov.eurzad.citizenregistry.officer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.gov.eurzad.citizenregistry.officer.model.Officer;

import java.util.Optional;

public interface OfficerRepository extends JpaRepository<Officer, Long> {
    public Optional<Officer> findByEmail(String email);

}
