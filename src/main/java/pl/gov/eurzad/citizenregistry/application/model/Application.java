package pl.gov.eurzad.citizenregistry.application.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.gov.eurzad.citizenregistry.citizen.model.Citizen;
import pl.gov.eurzad.citizenregistry.officer.model.Officer;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String applicationNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private ApplicationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.SUBMITTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", nullable = false)
    private Citizen citizen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_officer_id")
    private Officer assignedOfficer;

    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    private LocalDateTime resolvedAt;

    private String rejectionReason;

    private String notes;

    @PrePersist
    protected void onCreate(){
        submittedAt = LocalDateTime.now();
    }



}
