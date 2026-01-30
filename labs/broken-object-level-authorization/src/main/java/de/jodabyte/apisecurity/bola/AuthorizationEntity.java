package de.jodabyte.apisecurity.bola;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class AuthorizationEntity {

    private String ownerId;
}
