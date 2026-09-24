package pl.gov.eurzad.citizenregistry.citizen.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gov.eurzad.citizenregistry.citizen.mapper.CitizenMapper;
import pl.gov.eurzad.citizenregistry.citizen.dto.UpdateCitizenRequest;
import pl.gov.eurzad.citizenregistry.citizen.model.Citizen;
import pl.gov.eurzad.citizenregistry.citizen.repository.CitizenRepository;
import pl.gov.eurzad.citizenregistry.common.exception.DuplicateResourceException;
import pl.gov.eurzad.citizenregistry.common.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CitizenService {

    private final CitizenRepository citizenRepository;
    private final CitizenMapper citizenMapper;

    public Page<Citizen> findAll(Pageable page) {
        return citizenRepository.findAll(page);
    }

    public Citizen findByPesel(String pesel){
        return citizenRepository.findByPesel(pesel)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Citizen not found with PESEL: " + pesel)
                );
    }

    public Citizen findById(Long id){
        return citizenRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Citizen not found with id: " + id)
                );
    }

    @Transactional
    public Citizen create(Citizen citizen){
        if (citizenRepository.existsByPesel(citizen.getPesel())){
            throw new DuplicateResourceException("Citizen with PESEL " + citizen.getPesel() + " already exists");
        }
        return citizenRepository.save(citizen);
    }

    @Transactional
    public Citizen update(Long id, UpdateCitizenRequest request){
        Citizen existingCitizen = findById(id);
        citizenMapper.updateEntity(request, existingCitizen);
        return citizenRepository.save(existingCitizen);
    }
}

