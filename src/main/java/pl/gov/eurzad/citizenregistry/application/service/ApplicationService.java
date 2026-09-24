package pl.gov.eurzad.citizenregistry.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gov.eurzad.citizenregistry.application.mapper.ApplicationMapper;
import pl.gov.eurzad.citizenregistry.application.model.Application;
import pl.gov.eurzad.citizenregistry.application.repository.ApplicationRepository;
import pl.gov.eurzad.citizenregistry.citizen.model.Citizen;
import pl.gov.eurzad.citizenregistry.citizen.service.CitizenService;
import pl.gov.eurzad.citizenregistry.common.exception.ResourceNotFoundException;
import pl.gov.eurzad.citizenregistry.application.model.ApplicationStatus;
import pl.gov.eurzad.citizenregistry.application.model.ApplicationType;
import pl.gov.eurzad.citizenregistry.officer.model.Officer;
import pl.gov.eurzad.citizenregistry.officer.repository.OfficerRepository;

import java.time.LocalDateTime;
import java.time.Year;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CitizenService citizenService;
    private final OfficerRepository officerRepository;
    private final ApplicationMapper mapper;

    public Page<Application> findAll(Pageable pageable) {
        return applicationRepository.findAll(pageable);
    }

    public Page<Application> findByStatus(ApplicationStatus status, Pageable pageable) {
        return applicationRepository.findByStatus(status, pageable);
    }

    public Application findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
    }

    @Transactional
    public Application create(Long citizenId, ApplicationType type) {
        Citizen citizen = citizenService.findById(citizenId);

        Application application = new Application();
        application.setApplicationNumber(generateApplicationNumber());
        application.setType(type);
        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setCitizen(citizen);

        return applicationRepository.save(application);
    }

    @Transactional
    public Application assignOfficer(Long applicationId, Long officerId) {
        Application application = findById(applicationId);
        Officer officer = officerRepository.findById(officerId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Officer not found with id: " + officerId)
                );
        application.setAssignedOfficer(officer);
        return applicationRepository.save(application);

    }

    @Transactional
    public Application updateStatus(Long id, ApplicationStatus newStatus, String rejectionReason, String notes) {
        Application application = findById(id);

        validateStatusTransition(application.getStatus(), newStatus);
        if (newStatus == ApplicationStatus.REJECTED && (rejectionReason == null || rejectionReason.isBlank())) {
            throw new IllegalArgumentException("Rejection reason is required when rejecting an application");
        }

        application.setStatus(newStatus);
        application.setNotes(notes);

        if (newStatus == ApplicationStatus.REJECTED) {
            application.setRejectionReason(rejectionReason);
            application.setResolvedAt(LocalDateTime.now());
        }

        if(newStatus == ApplicationStatus.APPROVED){
            application.setResolvedAt(LocalDateTime.now());
        }

        return applicationRepository.save(application);
    }

    public void validateByKiciulkaStatusTransition(ApplicationStatus current, ApplicationStatus next) {
        if (current == ApplicationStatus.SUBMITTED && next == ApplicationStatus.IN_PROGRESS) {
            return;
        } else if (current == ApplicationStatus.IN_PROGRESS && next == ApplicationStatus.APPROVED) {
            return;
        } else if (current == ApplicationStatus.IN_PROGRESS && next == ApplicationStatus.REJECTED) {
            return;
        } else {
            throw new IllegalStateException("Cannot transition from  " + current + "to " + next);
        }
    }

    private void validateStatusTransition(ApplicationStatus current, ApplicationStatus next) {
        boolean valid = switch (current) {
            case SUBMITTED -> next == ApplicationStatus.IN_PROGRESS;
            case IN_PROGRESS -> next == ApplicationStatus.APPROVED || next == ApplicationStatus.REJECTED;
            case APPROVED, REJECTED -> false;
        };

        if (!valid) {
            throw new IllegalStateException("Cannot transition from " + current + " to " + next);
        }
    }

    private String generateApplicationNumber() {
        String prefix = "APP-" + Year.now().getValue() + "-";
        long count = applicationRepository.countByApplicationNumberStartingWith(prefix);
        return prefix + String.format("%05d", count + 1);
    }


}
