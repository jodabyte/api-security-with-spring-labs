package de.jodabyte.apisecurity.bfla;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BrokenFunctionLevelAuthorizationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("""
            Given a user with the necessary role "ROLE_ADMIN" to access the admin API
            When the user accesses the "/admin" endpoint
            Then the response status should be 200 OK
            """)
    void validRoleForAdminAPI() throws Exception {
        this.mockMvc.perform(get("/admin").with(jwt().authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("""
            Given a user with the role "ROLE_USER" which is not sufficient to access the admin API
            When the user accesses the "/admin" endpoint
            Then the response status should be 403 Forbidden
            """)
    void invalidRoleForAdminAPI() throws Exception {
        this.mockMvc.perform(get("/admin").with(jwt().authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))))
                .andExpect(status().isForbidden());
    }

}
