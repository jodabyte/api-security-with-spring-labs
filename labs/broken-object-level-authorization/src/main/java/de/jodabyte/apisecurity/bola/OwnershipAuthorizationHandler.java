package de.jodabyte.apisecurity.bola;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("ownership")
public class OwnershipAuthorizationHandler {

    /**
     * Check if the authenticated user has ownership of the given entity.
     *
     * @return true if the authenticated user is the owner of the entity, false otherwise. Returns null if the code
     * abstains from making a decision.
     */
    public Boolean auth(AuthorizationEntity entity, Authentication authentication) {
        if (entity == null || authentication == null) {
            return null;
        }

        return Objects.equals(authentication.getName(), entity.getOwnerId());
    }
}
