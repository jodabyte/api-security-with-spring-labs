package de.jodabyte.apisecurity.bopla;

import de.jodabyte.apisecurity.bopla.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BrokenObjectPropertyLevelAuthorizationTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Test
    @WithMockUser(authorities = {"WRITE"})
    @DisplayName("""
            Given a UserCreateDto schema that includes only required fields to successfully create a user
            When the user was created successfully
            Then the response should keep the data to the bare minimum, according to the business/functional requirements.
            """)
    void dtoMappingTest() throws Exception {
        var dto = new UserCreateDto("user-1", "name-1", "example@example.test");

        String actualAsString = this.mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto actual = objectMapper.readValue(actualAsString, UserDto.class);
        assertThatNoException().isThrownBy(() -> UUID.fromString(actual.uuid()));
    }

    @Test
    @WithMockUser(authorities = {"WRITE"})
    @DisplayName("""
            Given a UserCreateDto schema that includes only required fields to successfully create a user
            When an invalid email address is provided
            Then the response should be a Bad Request, as the email address does not meet the validation criteria.
            """)
    void invalidEmailTest() throws Exception {
        var dto = new UserCreateDto("user-2", "name-2", "invalid-email");

        this.mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    @DisplayName("""
            Given a user wants to access the internal user view, which is protected by the USER role
            When the user has the USER role
            Then the user should be able to access the internal user view and receive the expected user data.
            """)
    void getInternalUserViewTest() throws Exception {
        User expected = userService.createUser(this.userMapper.map(new UserCreateDto("user-3", "name-3", "example3@example.test")));

        String actualAsString = this.mockMvc.perform(get("/{id}", expected.getUuid()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto actual = objectMapper.readValue(actualAsString, UserDto.class);
        assertThat(actual.uuid()).isEqualTo(expected.getUuid());
    }

    @Test
    @WithMockUser(roles = {"PUBLIC"})
    @DisplayName("""
            Given a user wants to access the internal user view, which is protected by the USER role
            When the user has only the PUBLIC role
            Then the user should not be able to access the internal user view and receive a Forbidden response.
            """)
    void cannotAccessInternalViewWithPublicRole() throws Exception {
        User expected = userService.createUser(this.userMapper.map(new UserCreateDto("user-4", "name-4", "example4@example.test")));

        this.mockMvc.perform(get("/{id}", expected.getUuid()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"PUBLIC"})
    @DisplayName("""
            Given a user wants to access the public user view, which is protected by the PUBLIC role
            When the user has the PUBLIC role
            Then the user should be able to access the public user view and receive the expected public user data.
            """)
    void getPublicUserViewTest() throws Exception {
        User expected = userService.createUser(this.userMapper.map(new UserCreateDto("user-5", "name-5", "example5@example.test")));

        String actualAsString = this.mockMvc.perform(get("/public/{id}", expected.getUuid()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserPublicDto actual = objectMapper.readValue(actualAsString, UserPublicDto.class);
        assertThat(actual.getUuid()).isEqualTo(expected.getUuid());
    }

}
