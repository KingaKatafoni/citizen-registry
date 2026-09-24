package pl.gov.eurzad.citizenregistry.officer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfficerResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String department;
    private boolean active;
    private LocalDateTime createdAt;
}
