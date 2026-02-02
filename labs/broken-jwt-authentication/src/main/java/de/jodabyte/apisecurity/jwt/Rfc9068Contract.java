package de.jodabyte.apisecurity.jwt;

import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class defines constants based on the RFC 9068 specification for JWTs used in OAuth 2.0.
 * It includes standard header and payload values to ensure compliance with the RFC.
 */
public final class Rfc9068Contract {

    public static final String HEADER_TYPE = "at+jwt";
    public static final JwsAlgorithm HEADER_ALGORITHM = SignatureAlgorithm.RS256;
    public static final String PAYLOAD_ISSUER = "https://issuer.example.org";
    public static final List<String> PAYLOAD_AUDIENCE = List.of("https://my-audience.example.org");
    public static final String PAYLOAD_SUBJECT = "user@example.org";
    public static final Consumer<Map<String, Object>> PAYLOAD_CLAIMS = claims -> claims.put("client_id", "client-identifier");

    private Rfc9068Contract() {
    }
}
