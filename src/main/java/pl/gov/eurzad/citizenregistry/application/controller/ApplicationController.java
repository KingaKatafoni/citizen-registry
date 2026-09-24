package pl.gov.eurzad.citizenregistry.application.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.gov.eurzad.citizenregistry.application.dto.ApplicationResponse;
import pl.gov.eurzad.citizenregistry.application.dto.CreateApplicationRequest;
import pl.gov.eurzad.citizenregistry.application.dto.UpdateStatusRequest;
import pl.gov.eurzad.citizenregistry.application.mapper.ApplicationMapper;
import pl.gov.eurzad.citizenregistry.application.model.Application;
import pl.gov.eurzad.citizenregistry.application.model.ApplicationStatus;
import pl.gov.eurzad.citizenregistry.application.model.ApplicationType;
import pl.gov.eurzad.citizenregistry.application.service.ApplicationService;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;
    private final ApplicationMapper applicationMapper;

    @PostMapping
    public ResponseEntity<ApplicationResponse> create(@Valid @RequestBody CreateApplicationRequest request){
        ApplicationType type = ApplicationType.valueOf(request.getType().toUpperCase());
        Application saved = applicationService.create(request.getCitizenId(), type);

        return ResponseEntity.status(HttpStatus.CREATED).body(applicationMapper.toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<Page<ApplicationResponse>> findAll(@RequestParam(required = false)ApplicationStatus status, Pageable pageable){
        Page<Application> page = (status != null)
                ? applicationService.findByStatus(status, pageable)
                : applicationService.findAll(pageable);

        return ResponseEntity.ok(page.map(applicationMapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> findById(@PathVariable Long id){
        Application application = applicationService.findById(id);
        return ResponseEntity.ok(applicationMapper.toResponse(application));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<ApplicationResponse> assignOfficer(@PathVariable Long id, @RequestParam Long officerId){
        Application updated = applicationService.assignOfficer(id, officerId);

        return ResponseEntity.ok(applicationMapper.toResponse(updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request){
        ApplicationStatus status = ApplicationStatus.valueOf(request.getStatus().toUpperCase());
        Application updated = applicationService.updateStatus(id, status, request.getRejectionReason(),request.getNotes());

        return ResponseEntity.ok(applicationMapper.toResponse(updated));
    }
}
