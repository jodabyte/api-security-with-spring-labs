package de.jodabyte.apisecurity.bopla.model;

public record UserDto(
        String uuid,
        String username,
        String name,
        String email
) {
}
