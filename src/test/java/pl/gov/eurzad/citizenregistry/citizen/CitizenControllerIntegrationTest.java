package pl.gov.eurzad.citizenregistry.citizen;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.gov.eurzad.citizenregistry.auth.service.JwtService;
import pl.gov.eurzad.citizenregistry.citizen.dto.AddressDto;
import pl.gov.eurzad.citizenregistry.citizen.dto.CreateCitizenRequest;
import pl.gov.eurzad.citizenregistry.citizen.dto.UpdateCitizenRequest;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CitizenControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;


    // helper methods
    private String getAdminToken() {
        return "Bearer " + jwtService.generateToken("admin@test.pl", "ADMIN");
    }

    private String getOfficerToken() {
        return "Bearer " + jwtService.generateToken("officer@test.pl", "OFFICER");
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

    // POST -> /api/citizens

    @Test
    void shouldCreateCitizen() throws Exception {
        CreateCitizenRequest request = getCreateCitizenRequest();

        mockMvc.perform(post("/api/citizens")
                        .header("Authorization", getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.pesel").value("93021498990"));

    }

    @Test
    void shouldReturn409WhenDuplicatedPesel() throws Exception{
        CreateCitizenRequest request = getCreateCitizenRequest();

        mockMvc.perform(post("/api/citizens")
                .header("Authorization", getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/citizens")
                .header("Authorization", getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Citizen with PESEL 93021498990 already exists"));
    }

    @Test
    void shouldReturn400WhenInvalidData() throws Exception{
        String emptyBody = "{}";

        mockMvc.perform(post("/api/citizens")
                .header("Authorization", getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyBody))
                .andExpect(status().isBadRequest());
    }

    // GET -> /api/citizens

    @Test
    void shouldReturnPageCitizens() throws Exception{
        mockMvc.perform(get("/api/citizens")
                .header("Authorization", getAdminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists());
    }

    // GET -> /api/citizens/{pesel}

    @Test
    void shouldFindCitizenByPesel() throws Exception{
        // assets
        CreateCitizenRequest request = getCreateCitizenRequest();

        mockMvc.perform(post("/api/citizens")
                        .header("Authorization", getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

       String pesel = "93021498990";

        mockMvc.perform(get("/api/citizens/pesel/{pesel}", pesel)
                        .header("Authorization", getAdminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jan"));
    }


    @Test
    void shouldReturn404WhenPeselNotFound() throws Exception{
        CreateCitizenRequest request = getCreateCitizenRequest();

        mockMvc.perform(post("/api/citizens")
                        .header("Authorization", getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        String nonExistingPesel = "90897867564";

        mockMvc.perform(get("/api/citizens/pesel/{pesel}", nonExistingPesel)
                .header("Authorization", getAdminToken()))
                .andExpect(status().isNotFound());
    }



    // GET -> /api/citizens/{id}

    @Test
    void shouldFindCitizenById() throws Exception{
        // assets
        CreateCitizenRequest request = getCreateCitizenRequest();

        String responseJson = mockMvc.perform(post("/api/citizens")
                .header("Authorization", getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(responseJson).get("id").asLong();

        mockMvc.perform(get("/api/citizens/{id}", id)
                .header("Authorization", getAdminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jan"));
    }


    @Test
    void shouldReturn404WhenCitizenNotFound() throws Exception{

        mockMvc.perform(get("/api/citizens/999")
                .header("Authorization", getAdminToken()))
                .andExpect(status().isNotFound());
    }

    // PUT -> /api/citizens

    @Test
    void shouldReturnUpdatedCitizen() throws Exception{
        //assets
        CreateCitizenRequest createCitizenRequest = getCreateCitizenRequest();

        AddressDto address = new AddressDto();
        address.setStreet("Policka");
        address.setBuildingNumber("4");
        address.setCity("Poznan");
        address.setZipCode("00-034");
        address.setVoivodeship("wielkopolskie");

        String responseJson = mockMvc.perform(post("/api/citizens")
                        .header("Authorization", getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCitizenRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(responseJson).get("id").asLong();

        //update
        UpdateCitizenRequest updateCitizenRequest = new UpdateCitizenRequest();

        updateCitizenRequest.setFirstName("Andrzej");
        updateCitizenRequest.setLastName("Kowalski");
        updateCitizenRequest.setDateOfBirth(LocalDate.of(1993, 2, 14));
        updateCitizenRequest.setGender("MALE");
        updateCitizenRequest.setAddress(address);

        mockMvc.perform(put("/api/citizens/{id}", id)
                .header("Authorization", getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCitizenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Andrzej"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.address.street").value("Policka"));
    }

    @Test
    void shouldReturn404WhenUpdatedCitizenIdDoesntExist() throws Exception{

        //update
        UpdateCitizenRequest updateCitizenRequest = new UpdateCitizenRequest();

        AddressDto address = new AddressDto();
        address.setStreet("Marszalkowska");
        address.setBuildingNumber("1");
        address.setCity("Warszawa");
        address.setZipCode("00-001");
        address.setVoivodeship("mazowieckie");

        updateCitizenRequest.setFirstName("Andrzej");
        updateCitizenRequest.setLastName("Kowalski");
        updateCitizenRequest.setDateOfBirth(LocalDate.of(1993, 2, 14));
        updateCitizenRequest.setGender("MALE");
        updateCitizenRequest.setAddress(address);

        mockMvc.perform(put("/api/citizens/999")
                        .header("Authorization", getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCitizenRequest)))
                .andExpect(status().isNotFound());
    }

    // Security

    @Test
    void shouldAllowOfficerFindCitizens() throws Exception{
        mockMvc.perform(get("/api/citizens")
                .header("Authorization", getOfficerToken()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn403WhenNoToken() throws Exception{
        mockMvc.perform(get("/api/citizens"))
                .andExpect(status().isForbidden());
    }


}
