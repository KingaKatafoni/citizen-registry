package pl.gov.eurzad.citizenregistry.citizen.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String street;
    private String buildingNumber;
    private String apartmentNumber;
    private String city;
    private String zipCode;
    private String voivodeship;
}
