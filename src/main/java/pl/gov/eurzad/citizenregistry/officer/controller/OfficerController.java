package pl.gov.eurzad.citizenregistry.officer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.gov.eurzad.citizenregistry.officer.dto.OfficerResponse;
import pl.gov.eurzad.citizenregistry.officer.mapper.OfficerMapper;
import pl.gov.eurzad.citizenregistry.officer.model.Officer;
import pl.gov.eurzad.citizenregistry.officer.service.OfficerService;


@RestController
@RequestMapping("/api/officers")
@RequiredArgsConstructor
public class OfficerController {

    private final OfficerService officerService;
    private final OfficerMapper mapper;

    @GetMapping
    public ResponseEntity<Page<OfficerResponse>> findAllOfficers(Pageable pageable){
        Page<OfficerResponse> page = officerService.findAll(pageable)
                .map(mapper::toResponse);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfficerResponse> findById(@PathVariable Long id){
        Officer officer = officerService.findById(id);
        return ResponseEntity.ok(mapper.toResponse(officer));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<OfficerResponse> deactivateOfficer(@PathVariable Long id){
        Officer officer = officerService.deactivateOfficer(id);
        return ResponseEntity.ok(mapper.toResponse(officer));
    }
}
