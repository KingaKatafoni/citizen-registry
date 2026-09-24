package pl.gov.eurzad.citizenregistry.citizen.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCitizenRequest {

    @NotBlank
    @Pattern(regexp = "\\d{11}", message = "PESEL must be exactly 11 digits")
    private String pesel;

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @NotNull
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull
    private String gender;

    @Email
    private String email;

    private String phoneNumber;

    @Valid
    private AddressDto address;
}
