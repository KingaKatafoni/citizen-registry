package pl.gov.eurzad.citizenregistry.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.gov.eurzad.citizenregistry.application.dto.ApplicationResponse;
import pl.gov.eurzad.citizenregistry.application.model.Application;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(source = "citizen.id", target = "citizenId")
    @Mapping(source = "citizen.firstName", target = "citizenFirstName")
    @Mapping(source = "citizen.lastName", target = "citizenLastName")
    @Mapping(source = "assignedOfficer.id", target = "assignedOfficerId")
    ApplicationResponse toResponse(Application application);
}
