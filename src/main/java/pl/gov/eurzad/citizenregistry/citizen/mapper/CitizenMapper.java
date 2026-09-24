package pl.gov.eurzad.citizenregistry.citizen.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.gov.eurzad.citizenregistry.citizen.dto.CitizenResponse;
import pl.gov.eurzad.citizenregistry.citizen.dto.CreateCitizenRequest;
import pl.gov.eurzad.citizenregistry.citizen.dto.UpdateCitizenRequest;
import pl.gov.eurzad.citizenregistry.citizen.model.Citizen;

@Mapper(componentModel = "spring")
public interface CitizenMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Citizen toEntity(CreateCitizenRequest request);

    CitizenResponse toResponse(Citizen citizen);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pesel", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateCitizenRequest request, @MappingTarget Citizen citizen);
}
