package pl.gov.eurzad.citizenregistry.citizen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    @NotBlank
    private String street;

    @NotBlank
    private String buildingNumber;

    private String apartmentNumber;

    @NotBlank
    private String city;

    @NotBlank
    @Pattern(regexp = "\\d{2}-\\d{3}", message = "Zip code must match format XX-XXX")
    private String zipCode;

    @NotBlank
    private String voivodeship;
}
