package pl.gov.eurzad.citizenregistry.officer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.gov.eurzad.citizenregistry.auth.service.JwtService;
import pl.gov.eurzad.citizenregistry.officer.model.Officer;
import pl.gov.eurzad.citizenregistry.officer.model.Role;
import pl.gov.eurzad.citizenregistry.officer.repository.OfficerRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class OfficerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private OfficerRepository officerRepository;


    // helper methods
    private String getAdminToken() {
        return "Bearer " + jwtService.generateToken("admin@test.pl", "ADMIN");
    }

    private String getOfficerToken() {
        return "Bearer " + jwtService.generateToken("officer@test.pl", "OFFICER");
    }

    // GET -> page officers

    @Test
    void shouldReturnPageOfOfficers() throws Exception {
        mockMvc.perform(get("/api/officers")
                        .header("Authorization", getAdminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists());
    }

    // GET -> {id}
    @Test
    void shouldFindOfficerWithId() throws Exception {
        //given

        Officer officer = new Officer();
        officer.setEmail("karol.bak@officer.eurzad.pl");
        officer.setPassword("asd123");
        officer.setFirstName("Karol");
        officer.setLastName("Bąk");
        officer.setRole(Role.OFFICER);
        officer.setDepartment("Sprawy Nierozwiązane");

        Officer savedOfficer = officerRepository.save(officer);

        Long id = savedOfficer.getId();


        mockMvc.perform(get("/api/officers/{id}", id)
                        .header("Authorization", getAdminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Karol"));
    }

    // GET -> id 404 not found

    @Test
    void shouldReturn404WhenOfficerIdNotFound() throws Exception{
        mockMvc.perform(get("/api/officers/999")
                        .header("Authorization", getAdminToken()))
                .andExpect(status().isNotFound());
    }

    //PATCH -> {id}/deactivate

    @Test
    void shouldDeactivateOfficer() throws Exception{
        //given

        Officer officer = new Officer();
        officer.setEmail("karol.bak@officer.eurzad.pl");
        officer.setPassword("asd123");
        officer.setFirstName("Karol");
        officer.setLastName("Bąk");
        officer.setRole(Role.OFFICER);
        officer.setDepartment("Sprawy Nierozwiązane");

        Officer savedOfficer = officerRepository.save(officer);
        Long id = savedOfficer.getId();

        mockMvc.perform(patch("/api/officers/{id}/deactivate", id)
                .header("Authorization", getAdminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

    }

    // security ->  403
    @Test
    void shouldReturn403WhenForbiddenRole() throws Exception{
        mockMvc.perform(get("/api/officers")
                        .header("Authorization", getOfficerToken()))
                .andExpect(status().isForbidden());
    }

}
