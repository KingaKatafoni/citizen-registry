package pl.gov.eurzad.citizenregistry.application.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.gov.eurzad.citizenregistry.application.model.Application;
import pl.gov.eurzad.citizenregistry.application.model.ApplicationStatus;

public interface ApplicationRepository extends JpaRepository<Application, Long >{
    Page<Application> findByStatus(ApplicationStatus status, Pageable pageable);

    long countByApplicationNumberStartingWith(String prefix);
}
