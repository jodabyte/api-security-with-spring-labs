package de.jodabyte.apisecurity.jwt;

import com.nimbusds.jose.JOSEException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;

import java.security.NoSuchAlgorithmException;

@Configuration
public class AppConfig {

    private final SecretKeys secretKeys = SecretKeys.generateKeys();

    public AppConfig() throws NoSuchAlgorithmException, JOSEException {
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    /**
     * Creates a JwtDecoder bean configured with validators that conform to <a href="https://www.rfc-editor.org/rfc/rfc9068.html">RFC 9068</a>.
     * Spring Security provides a convenient way to <a href="https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#oauth2resourceserver-jwt-validation-rfc9068">configure the RFC 9068 validation</a>.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(secretKeys.publicKey())
                .validateType(false)
                .build();

        jwtDecoder.setJwtValidator(
                JwtValidators.createAtJwtValidator()
                        .audience("https://my-audience.example.org")
                        .clientId("client-identifier")
                        .issuer("https://issuer.example.org")
                        .build()
        );

        return jwtDecoder;
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return NimbusJwtEncoder.withKeyPair(secretKeys.publicKey(), secretKeys.privateKey())
                .build();
    }
}
