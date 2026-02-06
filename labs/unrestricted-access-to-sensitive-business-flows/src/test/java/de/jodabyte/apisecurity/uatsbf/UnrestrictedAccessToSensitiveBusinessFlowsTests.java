package de.jodabyte.apisecurity.uatsbf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UnrestrictedAccessToSensitiveBusinessFlowsTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("""
            Given a authenticated user with valid credentials
            When the user performs actions to the restricted flow multiple times within a short period
            Then the system should enforce rate limiting and return a 429 Too Many Requests status after a certain threshold is exceeded.
            """)
    void rateLimitTheRestrictedFlow() throws Exception {
        JwtRequestPostProcessor user1 = jwt().jwt(jwt -> jwt.subject("user1"));

        this.mockMvc.perform(post("/restricted-flow").with(user1))
                .andExpect(status().isOk());

        this.mockMvc.perform(post("/restricted-flow").with(user1))
                .andExpect(status().isTooManyRequests());

    }

}
