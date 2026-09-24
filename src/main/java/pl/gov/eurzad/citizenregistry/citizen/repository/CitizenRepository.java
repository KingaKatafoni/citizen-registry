package pl.gov.eurzad.citizenregistry.citizen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.gov.eurzad.citizenregistry.citizen.model.Citizen;

import java.util.Optional;

public interface CitizenRepository extends JpaRepository<Citizen, Long> {
    Optional<Citizen> findByPesel(String pesel);

    boolean existsByPesel(String pesel);
}
