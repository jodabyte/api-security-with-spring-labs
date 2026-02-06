package de.jodabyte.apisecurity.uatsbf;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableCaching
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CacheManager cacheManager) throws Exception {
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());

        http.addFilterBefore(new RestrictedFlowFilter(requestRateLimiter(cacheManager)), AuthorizationFilter.class);

        return http.build();
    }

    @Bean
    public RequestRateLimiter requestRateLimiter(CacheManager cacheManager) {
        return new RequestRateLimiter(cacheManager);
    }

    @Bean
    public JwtDecoder jwtDecoder() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
}
