package pl.gov.eurzad.citizenregistry.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.gov.eurzad.citizenregistry.application.dto.CreateApplicationRequest;
import pl.gov.eurzad.citizenregistry.application.dto.UpdateStatusRequest;
import pl.gov.eurzad.citizenregistry.application.model.Application;
import pl.gov.eurzad.citizenregistry.application.model.ApplicationStatus;
import pl.gov.eurzad.citizenregistry.application.model.ApplicationType;
import pl.gov.eurzad.citizenregistry.application.repository.ApplicationRepository;
import pl.gov.eurzad.citizenregistry.auth.service.JwtService;
import pl.gov.eurzad.citizenregistry.citizen.dto.AddressDto;
import pl.gov.eurzad.citizenregistry.citizen.dto.CreateCitizenRequest;
import pl.gov.eurzad.citizenregistry.citizen.model.Address;
import pl.gov.eurzad.citizenregistry.citizen.model.Citizen;
import pl.gov.eurzad.citizenregistry.citizen.model.Gender;
import pl.gov.eurzad.citizenregistry.citizen.repository.CitizenRepository;
import pl.gov.eurzad.citizenregistry.officer.model.Officer;
import pl.gov.eurzad.citizenregistry.officer.model.Role;
import pl.gov.eurzad.citizenregistry.officer.repository.OfficerRepository;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ApplicationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private OfficerRepository officerRepository;

    @Autowired
    private CitizenRepository citizenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // helper methods
    private String getAdminToken() {
        return "Bearer " + jwtService.generateToken("admin@test.pl", "ADMIN");
    }

    private String getOfficerToken() {
        return "Bearer " + jwtService.generateToken("officer@test.pl", "OFFICER");
    }

    private Officer createTestOfficer() {
        Officer officer = new Officer();
        officer.setEmail("karol.bak@eurzad.pl");
        officer.setPassword("test123");
        officer.setFirstName("Karol");
        officer.setLastName("Bąk");
        officer.setRole(Role.OFFICER);
        officer.setDepartment("Sprawy Nierozwiązane");
        return officerRepository.save(officer);
    }

    private Citizen createTestCitizen() {
        Citizen citizen = new Citizen();
        citizen.setPesel("90010112345");
        citizen.setFirstName("Anna");
        citizen.setLastName("Kowalska");
        citizen.setDateOfBirth(LocalDate.of(1990, 1, 1));
        citizen.setGender(Gender.FEMALE);
        citizen.setEmail("anna.kowalska@test.pl");
        citizen.setAddress(new Address(
                "Polna", "12", "3",
                "Poznań", "60-001", "wielkopolskie"
        ));
        return citizenRepository.save(citizen);
    }

    private static CreateCitizenRequest getCreateCitizenRequest() {
        AddressDto address = new AddressDto();
        address.setStreet("Marszalkowska");
        address.setBuildingNumber("1");
        address.setCity("Warszawa");
        address.setZipCode("00-001");
        address.setVoivodeship("mazowieckie");

        CreateCitizenRequest request = new CreateCitizenRequest();
        request.setFirstName("Jan");
        request.setLastName("Kowalski");
        request.setPesel("93021498990");
        request.setDateOfBirth(LocalDate.of(1993, 2, 14));
        request.setGender("MALE");
        request.setAddress(address);
        return request;
    }

    // GET -> page

    @Test
    void shouldReturnPageOfApplications() throws Exception {
        mockMvc.perform(get("/api/applications")
                        .header("Authorization", getAdminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists());
    }

    // GET -> id

    @Test
    void shouldFindApplicationById() throws Exception {
        Officer officer = createTestOfficer();
        Citizen citizen = createTestCitizen();

        Application application = new Application();
        application.setApplicationNumber("APL-001");
        application.setType(ApplicationType.REGISTRATION);
        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setCitizen(citizen);
        application.setAssignedOfficer(officer);
        application.setResolvedAt(LocalDateTime.now());

        Application saved = applicationRepository.save(application);
        Long id = saved.getId();

        mockMvc.perform(get("/api/applications/{id}", id)
                        .header("Authorization", getAdminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationNumber").value("APL-001"));

    }
    // GET -> id 404

    @Test
    void shouldReturn404WhenApplicationNotFound() throws Exception {
        mockMvc.perform(get("/api/applications/999")
                        .header("Authorization", getAdminToken()))
                .andExpect(status().isNotFound());
    }

    // POST -> create OK

    @Test
    void shouldCreateApplication() throws Exception {

        CreateCitizenRequest citizenRequest = getCreateCitizenRequest();
        String citizenResponseJson = mockMvc.perform(post("/api/citizens")
                        .header("Authorization", getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(citizenRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();


        CreateApplicationRequest applicationRequest = new CreateApplicationRequest();
        Long citizenId = objectMapper.readTree(citizenResponseJson).get("id").asLong();

        assertThat(citizenId).isNotNull();

        applicationRequest.setCitizenId(citizenId);
        applicationRequest.setType("id_card");


        mockMvc.perform(post("/api/applications")
                        .header("Authorization", getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(applicationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("ID_CARD"))
                .andExpect(jsonPath("$.citizenId").value(citizenId));

        assertThat(citizenId).isNotNull();
    }

    // POST -> Bad request 400 (wrong body)
    @Test
    void shouldReturn400WhenWrongBody() throws Exception {
        CreateCitizenRequest citizenRequest = getCreateCitizenRequest();
        String citizenResponseJson = mockMvc.perform(post("/api/citizens")
                        .header("Authorization", getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(citizenRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();


        CreateApplicationRequest applicationRequest = new CreateApplicationRequest();
        Long citizenId = objectMapper.readTree(citizenResponseJson).get("id").asLong();

        assertThat(citizenId).isNotNull();

        applicationRequest.setCitizenId(citizenId);
        applicationRequest.setType("dowod");


        mockMvc.perform(post("/api/applications")
                        .header("Authorization", getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(applicationRequest)))
                .andExpect(status().isBadRequest());
    }


    // PATCH -> {id}/assign
    @Test
    void shouldAssignOfficerToApplicationWithId() throws Exception {
        CreateCitizenRequest citizenRequest = getCreateCitizenRequest();
        String citizenResponseJson = mockMvc.perform(post("/api/citizens")
                        .header("Authorization", getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(citizenRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();


        CreateApplicationRequest applicationRequest = new CreateApplicationRequest();
        Long citizenId = objectMapper.readTree(citizenResponseJson).get("id").asLong();

        assertThat(citizenId).isNotNull();

        applicationRequest.setCitizenId(citizenId);
        applicationRequest.setType("id_card");

        String applicationResponseJson = mockMvc.perform(post("/api/applications")
                        .header("Authorization", getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(applicationRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString();

        Long applicationId = objectMapper.readTree(applicationResponseJson).get("id").asLong();

        Officer officer = new Officer();
        officer.setEmail("karol.bak@officer.eurzad.pl");
        officer.setPassword("asd123");
        officer.setFirstName("Karol");
        officer.setLastName("Bąk");
        officer.setRole(Role.OFFICER);
        officer.setDepartment("Sprawy Nierozwiązane");

        Officer savedOfficer = officerRepository.save(officer);

        Long officerId = savedOfficer.getId();

        mockMvc.perform(patch("/api/applications/{id}/assign", applicationId)
                        .header("Authorization", getAdminToken())
                        .param("officerId", officerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(applicationId));
    }

    // PATCH -> {id}/status

    @Test
    void shouldReturnOkWhenCorrectStatusChange() throws Exception {
        CreateCitizenRequest citizenRequest = getCreateCitizenRequest();
        String citizenResponseJson = mockMvc.perform(post("/api/citizens")
                        .header("Authorization", getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(citizenRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();


        CreateApplicationRequest applicationRequest = new CreateApplicationRequest();
        Long citizenId = objectMapper.readTree(citizenResponseJson).get("id").asLong();

        applicationRequest.setCitizenId(citizenId);
        applicationRequest.setType("id_card");

        assertThat(citizenId).isNotNull();

        String applicationResponseJson = mockMvc.perform(post("/api/applications")
                        .header("Authorization", getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(applicationRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString();

        Long applicationId = objectMapper.readTree(applicationResponseJson).get("id").asLong();

        UpdateStatusRequest updateStatusRequest = new UpdateStatusRequest();

        updateStatusRequest.setStatus("IN_PROGRESS");


        mockMvc.perform(patch("/api/applications/{id}/status", applicationId)
                        .header("Authorization", getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStatusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

    }

    // PATCH -> wrong status transition 409

    @Test
    void shouldReturn409WhenWrongTransition() throws Exception {

        CreateCitizenRequest citizenRequest = getCreateCitizenRequest();
        String citizenResponseJson = mockMvc.perform(post("/api/citizens")
                        .header("Authorization", getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(citizenRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();


        CreateApplicationRequest applicationRequest = new CreateApplicationRequest();
        Long citizenId = objectMapper.readTree(citizenResponseJson).get("id").asLong();

        applicationRequest.setCitizenId(citizenId);
        applicationRequest.setType("id_card");

        assertThat(citizenId).isNotNull();

        String applicationResponseJson = mockMvc.perform(post("/api/applications")
                        .header("Authorization", getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(applicationRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString();

        Long applicationId = objectMapper.readTree(applicationResponseJson).get("id").asLong();

        UpdateStatusRequest updateStatusRequest = new UpdateStatusRequest();

        updateStatusRequest.setStatus("APPROVED");


        mockMvc.perform(patch("/api/applications/{id}/status", applicationId)
                        .header("Authorization", getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStatusRequest)))
                .andExpect(status().isConflict());
    }
    // PATCH -> wrong id 404

    @Test
    void shouldReturn404WhenWrongApplicationId() throws Exception {

        Long applicationId = 999L;

        UpdateStatusRequest updateStatusRequest = new UpdateStatusRequest();

        updateStatusRequest.setStatus("IN_PROGRESS");


        mockMvc.perform(patch("/api/applications/{id}/status", applicationId)
                        .header("Authorization", getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStatusRequest)))
                .andExpect(status().isNotFound());
    }
}
