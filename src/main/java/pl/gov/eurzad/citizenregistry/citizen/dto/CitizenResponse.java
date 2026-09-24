package pl.gov.eurzad.citizenregistry.citizen.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CitizenResponse {

    private Long id;
    private String pesel;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String email;
    private String phoneNumber;
    private AddressDto address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
