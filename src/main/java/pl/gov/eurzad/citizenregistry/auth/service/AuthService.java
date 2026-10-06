package pl.gov.eurzad.citizenregistry.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gov.eurzad.citizenregistry.auth.dto.AuthResponse;
import pl.gov.eurzad.citizenregistry.auth.dto.LoginRequest;
import pl.gov.eurzad.citizenregistry.auth.dto.RegisterRequest;
import pl.gov.eurzad.citizenregistry.common.exception.DuplicateResourceException;
import pl.gov.eurzad.citizenregistry.officer.model.Officer;
import pl.gov.eurzad.citizenregistry.officer.model.Role;
import pl.gov.eurzad.citizenregistry.officer.repository.OfficerRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OfficerRepository officerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        Officer officer = officerRepository.findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new IllegalArgumentException("Invalid email or password")
                );

        if (!officer.isActive()) {
            throw new IllegalStateException("Account is deactivated");
        }

        if (!passwordEncoder.matches(request.getPassword(), officer.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(officer.getEmail(), officer.getRole().name());
        return new AuthResponse(token, officer.getEmail(), officer.getRole().name());
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (officerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException(("Officer with email " + request.getEmail() + " already exists"));
        }

        Officer officer = new Officer();
        officer.setEmail(request.getEmail());
        officer.setPassword(passwordEncoder.encode(request.getPassword()));
        officer.setFirstName(request.getFirstName());
        officer.setLastName(request.getLastName());
        officer.setRole(Role.valueOf(request.getRole().toUpperCase()));
        officer.setDepartment(request.getDepartment());
        officer.setActive(true);

        officerRepository.save(officer);

        String token = jwtService.generateToken(officer.getEmail(), officer.getRole().name());

        return new AuthResponse(token, officer.getEmail(), officer.getRole().name());
    }
}
