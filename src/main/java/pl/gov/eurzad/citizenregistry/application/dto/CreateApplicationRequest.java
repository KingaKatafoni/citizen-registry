package pl.gov.eurzad.citizenregistry.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateApplicationRequest {

    @NotNull
    private Long citizenId;

    @NotNull
    private String type;
}
