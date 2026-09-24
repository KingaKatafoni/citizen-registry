package pl.gov.eurzad.citizenregistry.officer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.gov.eurzad.citizenregistry.officer.dto.OfficerResponse;
import pl.gov.eurzad.citizenregistry.officer.model.Officer;
import pl.gov.eurzad.citizenregistry.officer.model.Role;

@Mapper(componentModel = "spring")
public interface OfficerMapper {

    @Mapping(source = "role", target = "role")
    OfficerResponse toResponse(Officer officer);
    

}
