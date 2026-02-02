package de.jodabyte.apisecurity.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static de.jodabyte.apisecurity.jwt.Rfc9068Contract.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class BrokenJwtAuthenticationApplicationTests {

    private final String ENDPOINT = "/hello";
    private final String JTI = "a1bc9ae2-e7fe-4910-9569-f35bfd41089b";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Test
    @DisplayName("""
            Given a valid JWT conform to RFC 9068
            When accessing a protected resource
            Then access is granted
            """)
    void validJwt() throws Exception {
        this.mockMvc.perform(get(ENDPOINT)
                        .header(AUTHORIZATION, createToken(createValidHeader(), createPayloadBuilder().build())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("""
            Given a JWT with an invalid signing algorithm in the header
            When accessing a protected resource
            Then access is denied with 401 Unauthorized
            """)
    void invalidSigningAlgorithmInHeader() throws Exception {
        String token = "Bearer eyJhbGciOiJub25lIiwidHlwIjoiYXQrand0In0.eyJ0ZXN0IjoidGVzdCJ9.";

        this.mockMvc.perform(get(ENDPOINT)
                        .header(AUTHORIZATION, token))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(
                        HttpHeaders.WWW_AUTHENTICATE,
                        containsString("Unsupported algorithm of none")
                ));
    }

    @ParameterizedTest
    @ValueSource(strings = {"at+jwt", "application/at+jwt"})
    @DisplayName("""
            Given a JWT with a supported typ value in the header
            When accessing a protected resource
            Then access is granted
            """)
    void verifySupportedTokenTypeInHeader(String tokenType) throws Exception {
        JwsHeader header = JwsHeader.with(HEADER_ALGORITHM)
                .type(tokenType)
                .build();

        this.mockMvc.perform(get(ENDPOINT)
                        .header(AUTHORIZATION, createToken(header, createPayloadBuilder().build())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("""
            Given a JWT with an invalid typ value in the header
            When accessing a protected resource
            Then access is denied with 401 Unauthorized
            """)
    void invalidTokenTypeInHeader() throws Exception {
        JwsHeader header = JwsHeader.with(HEADER_ALGORITHM)
                .type("invalid-type")
                .build();

        this.mockMvc.perform(get(ENDPOINT)
                        .header(AUTHORIZATION, createToken(header, createPayloadBuilder().build())))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(
                        HttpHeaders.WWW_AUTHENTICATE,
                        containsString("the given typ value needs to be one of [at+jwt, application/at+jwt]")
                ));
    }

    @Test
    @DisplayName("""
            Given a JWT with an invalid iss claim in the payload
            When accessing a protected resource
            Then access is denied with 401 Unauthorized
            """)
    void invalidIssuerInPayload() throws Exception {
        JwtClaimsSet.Builder payload = createPayloadBuilder();
        payload.issuer("invalid-issuer");

        this.mockMvc.perform(get(ENDPOINT)
                        .header(AUTHORIZATION, createToken(createValidHeader(), payload.build())))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(
                        HttpHeaders.WWW_AUTHENTICATE,
                        containsString("The iss claim is not valid")
                ));
    }

    @Test
    @DisplayName("""
            Given a JWT with an expired exp claim in the payload
            When accessing a protected resource
            Then access is denied with 401 Unauthorized
            """)
    void invalidExpiresAtInPayload() throws Exception {
        JwtClaimsSet.Builder payload = createPayloadBuilder();

        // By default, Resource Server configures a clock skew of 60 seconds.
        Instant instant = Instant.now().minusSeconds(61);
        payload.expiresAt(instant.plusSeconds(1))
                .issuedAt(instant);

        this.mockMvc.perform(get(ENDPOINT)
                        .header(AUTHORIZATION, createToken(createValidHeader(), payload.build())))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(
                        HttpHeaders.WWW_AUTHENTICATE,
                        containsString("Jwt expired at")
                ));
    }

    @Test
    @DisplayName("""
            Given a JWT with an unknown aud claim in the payload
            When accessing a protected resource
            Then access is denied with 401 Unauthorized
            """)
    void invalidAudienceInPayload() throws Exception {
        JwtClaimsSet.Builder payload = createPayloadBuilder();
        payload.audience(List.of("https://another-audience.example.org"));

        this.mockMvc.perform(get(ENDPOINT)
                        .header(AUTHORIZATION, createToken(createValidHeader(), payload.build())))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(
                        HttpHeaders.WWW_AUTHENTICATE,
                        containsString("The aud claim is not valid")
                ));
    }

    @Test
    @DisplayName("""
            Given a JWT with a missing sub claim in the payload
            When accessing a protected resource
            Then access is denied with 401 Unauthorized
            """)
    void invalidSubjectInPayload() throws Exception {
        Instant issued = Instant.now();
        JwtClaimsSet.Builder jwtWithMissingSubject = JwtClaimsSet.builder()
                .issuer(PAYLOAD_ISSUER)
                .expiresAt(issued.plusSeconds(300))
                .audience(PAYLOAD_AUDIENCE)
                .claims(PAYLOAD_CLAIMS)
                .issuedAt(issued)
                .id(JTI);

        this.mockMvc.perform(get(ENDPOINT)
                        .header(AUTHORIZATION, createToken(createValidHeader(), jwtWithMissingSubject.build())))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(
                        HttpHeaders.WWW_AUTHENTICATE,
                        containsString("sub must have a value")
                ));
    }

    @Test
    @DisplayName("""
            Given a JWT with an unknown client_id claim in the payload
            When accessing a protected resource
            Then access is denied with 401 Unauthorized
            """)
    void invalidClientIdClaimInPayload() throws Exception {
        JwtClaimsSet.Builder payload = createPayloadBuilder();
        payload.claim("client_id", "unknown-client-identifier");

        this.mockMvc.perform(get(ENDPOINT)
                        .header(AUTHORIZATION, createToken(createValidHeader(), payload.build())))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(
                        HttpHeaders.WWW_AUTHENTICATE,
                        containsString("client_id is not valid")
                ));
    }

    @Test
    @DisplayName("""
            Given a JWT with a missing jti claim in the payload
            When accessing a protected resource
            Then access is denied with 401 Unauthorized
            """)
    void invalidJwtIdInPayload() throws Exception {
        Instant issued = Instant.now();
        JwtClaimsSet.Builder payload = JwtClaimsSet.builder()
                .issuer(PAYLOAD_ISSUER)
                .expiresAt(issued.plusSeconds(300))
                .audience(PAYLOAD_AUDIENCE)
                .subject(PAYLOAD_SUBJECT)
                .claims(PAYLOAD_CLAIMS)
                .issuedAt(issued);

        this.mockMvc.perform(get(ENDPOINT)
                        .header(AUTHORIZATION, createToken(createValidHeader(), payload.build())))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(
                        HttpHeaders.WWW_AUTHENTICATE,
                        containsString("jti must have a value")
                ));
    }

    private String createToken(JwsHeader jwsHeader, JwtClaimsSet jwtClaimsSet) {
        JwtEncoderParameters parameters = JwtEncoderParameters.from(jwsHeader, jwtClaimsSet);
        return "Bearer " + jwtEncoder.encode(parameters).getTokenValue();
    }

    private JwsHeader createValidHeader() {
        return JwsHeader.with(HEADER_ALGORITHM)
                .type(HEADER_TYPE)
                .build();
    }

    private JwtClaimsSet.Builder createPayloadBuilder() {
        Instant issued = Instant.now();
        return JwtClaimsSet.builder()
                .issuer(PAYLOAD_ISSUER)
                .expiresAt(issued.plusSeconds(300))
                .audience(PAYLOAD_AUDIENCE)
                .subject(PAYLOAD_SUBJECT)
                .claims(PAYLOAD_CLAIMS)
                .issuedAt(issued)
                .id(JTI);
    }
}
