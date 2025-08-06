package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserUtils userUtils;

    private User testUser;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject(userUtils.getAdminUser().getEmail()));
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");

        testUser.setPasswordDigest("$2a$10$DummyHashedPasswordValue");
        userRepository.save(testUser);
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/users")
                        .with(token))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        var result = mockMvc.perform(get("/api/users/{id}", testUser.getId())
                        .with(token))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("email").isEqualTo(testUser.getEmail())
        );
    }

    @Test
    public void testCreate() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setFirstName("Alice");
        createDTO.setLastName("Wonderland");
        createDTO.setEmail("alice@example.com");
        createDTO.setPassword("secret-password");

        var request = post("/api/users")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(createDTO))
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.claim("sub", "hexlet@example.com")));

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        UserDTO createdUser = om.readValue(body, UserDTO.class);
        assertThat(createdUser.getFirstName()).isEqualTo(createDTO.getFirstName());
        assertThat(createdUser.getLastName()).isEqualTo(createDTO.getLastName());
        assertThat(createdUser.getEmail()).isEqualTo(createDTO.getEmail());

        var userOptional = userRepository.findByEmail(createDTO.getEmail());
        assertThat(userOptional).isPresent();
        var user = userOptional.get();
        assertThat(user.getPasswordDigest()).isNotEqualTo(createDTO.getPassword());
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        String newFirstName = "UpdatedName";
        String newLastName = "UpdatedLastName";
        updateDTO.setFirstName(JsonNullable.of(newFirstName));
        updateDTO.setLastName(JsonNullable.of(newLastName));

        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        var request = put("/api/users/{id}", testUser.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(updateDTO));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        UserDTO updatedUser = om.readValue(body, UserDTO.class);
        Optional<User> byId = userRepository.findById(testUser.getId());

        assertThat(byId.get().getFirstName()).isEqualTo(newFirstName);
        assertThat(byId.get().getLastName()).isEqualTo(newLastName);

        assertThat(updatedUser.getFirstName()).isEqualTo(newFirstName);
        assertThat(updatedUser.getLastName()).isEqualTo(newLastName);
    }

    @Test
    public void testDelete() throws Exception {
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
        var request = delete("/api/users/{id}", testUser.getId())
                .with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        var userOptional = userRepository.findByEmail(testUser.getEmail());
        assertThat(userOptional).isEmpty();
    }

    @Test
    public void testIndexWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShowWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isUnauthorized());
    }
}
