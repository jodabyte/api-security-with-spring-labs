package de.jodabyte.apisecurity.bopla.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateDto(
        @NotBlank
        @Size(min = 2, max = 10)
        String username,
        @NotBlank
        String name,
        @Email
        String email
) {
}
