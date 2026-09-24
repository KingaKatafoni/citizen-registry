package pl.gov.eurzad.citizenregistry.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {

    private Long id;
    private String applicationNumber;
    private String type;
    private String status;
    private Long citizenId;
    private String citizenFirstName;
    private String citizenLastName;
    private Long assignedOfficerId;
    private LocalDateTime submittedAt;
    private LocalDateTime resolvedAt;
    private String rejectionReason;
    private String notes;
}
