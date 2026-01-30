package de.jodabyte.apisecurity.bola;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BrokenObjectLevelAuthorizationTests {

    private final String OWNING_USER = "user-1";
    private final String ANOTHER_USER = "user-2";
    private final String ROLE_MANAGER = "ROLE_MANAGER";
    private final String ROLE_USER = "ROLE_USER";

    @Autowired
    private DocumentRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("""
            Given a user owns a document and has the correct role
            When the user requests the document
            Then the user is allowed to access the document
            """)
    void hasAllPermissionsToAccessDocument() throws Exception {
        Document owningDocument = new Document();
        owningDocument.setOwnerId(OWNING_USER);
        owningDocument.setDescription("Test Document 1");
        Document anotherDocument = new Document();
        anotherDocument.setOwnerId(ANOTHER_USER);
        anotherDocument.setDescription("Test Document 2");

        owningDocument = this.repository.save(owningDocument);
        anotherDocument = this.repository.save(anotherDocument);

        this.mockMvc.perform(
                        get("/documents/" + owningDocument.getId())
                                .with(createJwt(OWNING_USER, ROLE_MANAGER))
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("""
            Given a user does own a document but has the incorrect role
            When the user requests the document
            Then the user is forbidden to access the document
            """)
    void incorrectRole() throws Exception {
        Document owningDocument = new Document();
        owningDocument.setOwnerId(OWNING_USER);
        owningDocument.setDescription("Test Document 1");
        Document anotherDocument = new Document();
        anotherDocument.setOwnerId(ANOTHER_USER);
        anotherDocument.setDescription("Test Document 2");

        owningDocument = this.repository.save(owningDocument);
        anotherDocument = this.repository.save(anotherDocument);

        this.mockMvc.perform(
                        get("/documents/" + owningDocument.getId())
                                .with(createJwt(OWNING_USER, ROLE_USER))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("""
            Given a user does not own a document but has the correct role
            When the user requests the document
            Then the user is forbidden to access the document
            """)
    void userDoesNotOwnDocument() throws Exception {
        Document owningDocument = new Document();
        owningDocument.setOwnerId(OWNING_USER);
        owningDocument.setDescription("Test Document 1");
        Document anotherDocument = new Document();
        anotherDocument.setOwnerId(ANOTHER_USER);
        anotherDocument.setDescription("Test Document 2");

        owningDocument = this.repository.save(owningDocument);
        anotherDocument = this.repository.save(anotherDocument);

        this.mockMvc.perform(
                        get("/documents/" + anotherDocument.getId())
                                .with(createJwt(OWNING_USER, ROLE_MANAGER))
                )
                .andExpect(status().isForbidden());
    }


    /**
     * Helper method to create a JWT with specified user ID and roles.
     */
    private JwtRequestPostProcessor createJwt(String userId, String... roles) {
        return jwt().jwt(jwt -> jwt.subject(userId))
                .authorities(AuthorityUtils.createAuthorityList(roles));
    }

}
