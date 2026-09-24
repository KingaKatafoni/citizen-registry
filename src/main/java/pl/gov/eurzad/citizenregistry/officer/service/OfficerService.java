package pl.gov.eurzad.citizenregistry.officer.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gov.eurzad.citizenregistry.common.exception.ResourceNotFoundException;
import pl.gov.eurzad.citizenregistry.officer.mapper.OfficerMapper;
import pl.gov.eurzad.citizenregistry.officer.model.Officer;
import pl.gov.eurzad.citizenregistry.officer.repository.OfficerRepository;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class OfficerService {

    private final OfficerRepository officerRepository;


    public Page<Officer> findAll(Pageable pageable){
        return officerRepository.findAll(pageable);
    }


    public Officer findById(Long id){
        return officerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Officer with id " + id + " doesn't exist")
        );
    }

    @Transactional
    public Officer deactivateOfficer(Long id){
        Officer current = findById(id);

        if (!current.isActive()){
            throw new IllegalStateException("Officer is already deactivated");
        }

        current.setActive(false);
        return officerRepository.save(current);
    }

}
