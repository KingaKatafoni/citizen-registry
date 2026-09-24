package pl.gov.eurzad.citizenregistry.citizen.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.gov.eurzad.citizenregistry.citizen.mapper.CitizenMapper;
import pl.gov.eurzad.citizenregistry.citizen.dto.CitizenResponse;
import pl.gov.eurzad.citizenregistry.citizen.dto.CreateCitizenRequest;
import pl.gov.eurzad.citizenregistry.citizen.dto.UpdateCitizenRequest;
import pl.gov.eurzad.citizenregistry.citizen.model.Citizen;
import pl.gov.eurzad.citizenregistry.citizen.service.CitizenService;

@RestController
@RequestMapping("/api/citizens")
@RequiredArgsConstructor
public class CitizenController {

    private final CitizenService citizenService;
    private final CitizenMapper citizenMapper;

    @PostMapping
    public ResponseEntity<CitizenResponse> create(@Valid @RequestBody CreateCitizenRequest request){
        Citizen citizen = citizenMapper.toEntity(request);
        Citizen saved = citizenService.create(citizen);
        return ResponseEntity.status(HttpStatus.CREATED).body(citizenMapper.toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<Page<CitizenResponse>> findAll(Pageable pageable){
        Page<CitizenResponse> page = citizenService.findAll(pageable)
                .map(citizenMapper::toResponse);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitizenResponse> findById(@PathVariable Long id){
        Citizen citizen = citizenService.findById(id);
        return ResponseEntity.ok(citizenMapper.toResponse(citizen));
    }

    @GetMapping("/pesel/{pesel}")
    public ResponseEntity<CitizenResponse> findByPesel(@PathVariable String pesel){
        Citizen citizen = citizenService.findByPesel(pesel);
        return ResponseEntity.ok(citizenMapper.toResponse(citizen));
    }

    @PutMapping
    public ResponseEntity<CitizenResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCitizenRequest request){
        Citizen updated = citizenService.update(id, request);
        return ResponseEntity.ok(citizenMapper.toResponse(updated));
    }
}
